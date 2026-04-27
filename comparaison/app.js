const REST_BASE_URL = "http://localhost:8081";
const GRAPHQL_HTTP_URL = "http://localhost:8080/graphql";
const GRAPHQL_WS_URL = "ws://localhost:8080/graphql-ws";

const REST_QUERY_URL = `${REST_BASE_URL}/api/livres?genre=FICTION&disponible=true`;
const GQL_QUERY = `query {
  livres(genre: ROMAN, disponible: true) {
    id
    titre
    disponible
  }
}`;

const GQL_SUB_QUERY = `subscription($genre: Genre) {
  livreDisponible(genre: $genre) {
    titre
    auteur { nom }
  }
}`;

function el(id) {
  return document.getElementById(id);
}

function nowHHMMSS() {
  const d = new Date();
  return d.toLocaleTimeString("fr-FR", { hour12: false });
}

function formatBytes(bytes) {
  if (!Number.isFinite(bytes)) return "—";
  if (bytes < 1024) return `${bytes} B`;
  const kb = bytes / 1024;
  return `${kb.toFixed(1)} KB`;
}

function prettyJson(obj, maxLen = 1600) {
  const text = JSON.stringify(obj, null, 2);
  if (text.length <= maxLen) return text;
  return text.slice(0, maxLen) + "\n…";
}

function approxFieldCount(sample) {
  if (!sample || typeof sample !== "object") return 0;
  return Object.keys(sample).length;
}

async function timedFetchJson(url, options) {
  const t0 = performance.now();
  const res = await fetch(url, options);
  const text = await res.text();
  const t1 = performance.now();
  let json = null;
  try {
    json = text ? JSON.parse(text) : null;
  } catch {
    // keep null
  }
  return {
    ok: res.ok,
    status: res.status,
    timeMs: Math.round(t1 - t0),
    bytes: new TextEncoder().encode(text).length,
    text,
    json,
  };
}

async function runRest() {
  el("restJson").textContent = "Chargement…";
  const r = await timedFetchJson(REST_QUERY_URL);
  if (!r.ok) {
    el("restJson").textContent = `Erreur REST ${r.status}\n${r.text}`;
    el("restTime").textContent = `${r.timeMs} ms`;
    el("restTime2").textContent = `${r.timeMs} ms`;
    return;
  }

  const arr = Array.isArray(r.json) ? r.json : [];
  const first = arr[0] ?? null;

  el("restJson").textContent = prettyJson(first ?? r.json);
  const fields = approxFieldCount(first);
  el("restFieldsMeta").textContent = `~${fields} champs retournés`;

  el("restSize").textContent = formatBytes(r.bytes);
  el("restSizeMeta").textContent = `~${fields} champs`;
  el("restTime").textContent = `${r.timeMs} ms`;

  el("restSize2").textContent = formatBytes(r.bytes);
  el("restTime2").textContent = `${r.timeMs} ms`;
}

async function runGraphql() {
  el("gqlQuery").textContent = GQL_QUERY;
  el("gqlJson").textContent = "Chargement…";

  const r = await timedFetchJson(GRAPHQL_HTTP_URL, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ query: GQL_QUERY }),
  });

  if (!r.ok) {
    el("gqlJson").textContent = `Erreur GraphQL HTTP ${r.status}\n${r.text}`;
    el("gqlTime").textContent = `${r.timeMs} ms`;
    el("gqlTime2").textContent = `${r.timeMs} ms`;
    return;
  }

  const livres = r.json?.data?.livres;
  const first = Array.isArray(livres) ? livres[0] : null;

  el("gqlJson").textContent = prettyJson(r.json?.data ?? r.json);
  const fields = approxFieldCount(first);
  el("gqlFieldsMeta").textContent = `~${fields} champs retournés`;

  el("gqlSize").textContent = formatBytes(r.bytes);
  el("gqlSizeMeta").textContent = `~${fields} champs`;
  el("gqlTime").textContent = `${r.timeMs} ms`;

  el("gqlSize2").textContent = formatBytes(r.bytes);
  el("gqlTime2").textContent = `${r.timeMs} ms`;
}

function setWsStatus(kind, text) {
  const node = el("wsStatus");
  node.textContent = text;
  node.classList.remove("status__value--ok", "status__value--warn", "status__value--bad");
  if (kind === "ok") node.classList.add("status__value--ok");
  if (kind === "warn") node.classList.add("status__value--warn");
  if (kind === "bad") node.classList.add("status__value--bad");
}

function pushEvent({ type, title, desc, meta }) {
  const wrap = el("timeline");
  const card = document.createElement("div");
  card.className = `event ${type === "return" ? "event--return" : ""}`;

  const icon = type === "return" ? "undo" : "menu_book";
  const time = nowHHMMSS();

  card.innerHTML = `
    <div class="event__time">
      <span class="material-symbols-outlined">${icon}</span>
      <span>${time}</span>
    </div>
    <div class="event__title">${title}</div>
    <div class="event__desc">${desc}</div>
    <div class="event__meta">${meta ?? ""}</div>
  `.trim();

  wrap.prepend(card);
  // keep at most 10 cards
  while (wrap.children.length > 10) wrap.removeChild(wrap.lastElementChild);
}

function startGraphqlSubscription() {
  const livePill = el("livePill");
  const liveDot = el("liveDot");

  let ws;
  try {
    ws = new WebSocket(GRAPHQL_WS_URL, "graphql-transport-ws");
  } catch (e) {
    setWsStatus("bad", "WebSocket indisponible");
    livePill.classList.add("off");
    liveDot.title = String(e);
    return;
  }

  let acknowledged = false;

  ws.onopen = () => {
    setWsStatus("warn", "Handshake…");
    ws.send(JSON.stringify({ type: "connection_init", payload: {} }));
  };

  ws.onmessage = (ev) => {
    let msg;
    try {
      msg = JSON.parse(ev.data);
    } catch {
      return;
    }

    if (msg.type === "connection_ack") {
      acknowledged = true;
      setWsStatus("ok", "Connecté");
      livePill.classList.remove("off");
      ws.send(
        JSON.stringify({
          id: "1",
          type: "subscribe",
          payload: {
            query: GQL_SUB_QUERY,
            variables: { genre: "ROMAN" },
          },
        }),
      );
      return;
    }

    if (msg.type === "next") {
      const livre = msg.payload?.data?.livreDisponible;
      if (!livre) return;
      pushEvent({
        type: "available",
        title: "NOUVEAU LIVRE DISPONIBLE",
        desc: `"${livre.titre}"`,
        meta: `Auteur : ${livre.auteur?.nom ?? "—"}`,
      });
      return;
    }

    if (msg.type === "ping") {
      ws.send(JSON.stringify({ type: "pong" }));
      return;
    }

    if (msg.type === "error") {
      setWsStatus("bad", "Erreur WS");
      livePill.classList.add("off");
      return;
    }
  };

  ws.onerror = () => {
    setWsStatus("bad", "Erreur WebSocket");
    livePill.classList.add("off");
  };

  ws.onclose = () => {
    if (!acknowledged) setWsStatus("bad", "Non connecté");
    else setWsStatus("warn", "Déconnecté");
    livePill.classList.add("off");
  };
}

async function bootstrap() {
  // Starter cards (like the screenshot)
  pushEvent({
    type: "available",
    title: "NOUVEAU LIVRE DISPONIBLE",
    desc: `"Le Petit Prince"`,
    meta: "Auteur : Antoine de Saint-Exupéry",
  });
  pushEvent({
    type: "return",
    title: "RETOUR DE LIVRE",
    desc: `"1984"`,
    meta: "Devient disponible",
  });

  await Promise.allSettled([runRest(), runGraphql()]);
  startGraphqlSubscription();
}

bootstrap();

