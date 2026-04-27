// Page servie depuis REST: http://localhost:8081/comparaison/
const DEFAULTS = {
  restUrl: "/api/livres",
  restMethod: "GET",
  restBody: "",
  gqlHttp: "http://localhost:8080/graphql",
  gqlQuery: `query {
  livres(genre: ROMAN, disponible: true) {
    id
    titre
    disponible
  }
}`,
};

const GRAPHQL_WS_URL = "ws://localhost:8080/graphql-ws";
const REQUESTS_REST_TXT = "/comparaison/requests/rest";
const REQUESTS_GQL_TXT = "/comparaison/requests/graphql";

function el(id) {
  return document.getElementById(id);
}

function nowHHMMSS() {
  return new Date().toLocaleTimeString("fr-FR", { hour12: false });
}

function formatBytes(bytes) {
  if (!Number.isFinite(bytes)) return "—";
  if (bytes < 1024) return `${bytes} B`;
  return `${(bytes / 1024).toFixed(1)} KB`;
}

function safeJsonParse(text) {
  try {
    return text ? JSON.parse(text) : null;
  } catch {
    return null;
  }
}

function prettyJson(obj, maxLen = 2200) {
  const text = JSON.stringify(obj, null, 2);
  if (text.length <= maxLen) return text;
  return text.slice(0, maxLen) + "\n…";
}

function approxFieldCount(sample) {
  if (!sample || typeof sample !== "object") return 0;
  return Object.keys(sample).length;
}

function headersToMetrics(headers) {
  const internal = Number(headers.get("x-internal-calls"));
  const db = Number(headers.get("x-db-statements"));
  const elapsed = Number(headers.get("x-elapsed-ms"));
  return {
    internalCalls: Number.isFinite(internal) ? internal : null,
    dbStatements: Number.isFinite(db) ? db : null,
    serverElapsedMs: Number.isFinite(elapsed) ? elapsed : null,
  };
}

async function timedFetch(url, options) {
  const t0 = performance.now();
  const res = await fetch(url, options);
  const text = await res.text();
  const t1 = performance.now();
  return {
    ok: res.ok,
    status: res.status,
    timeMs: Math.round(t1 - t0),
    bytes: new TextEncoder().encode(text).length,
    text,
    json: safeJsonParse(text),
    headers: res.headers,
  };
}

function setCallout(side, { kind, title, desc, icon }) {
  const rootId = side === "rest" ? "rest" : "gql";
  el(`${rootId}Callout`).classList.toggle("callout--rest", side === "rest");
  el(`${rootId}Callout`).classList.toggle("callout--gql", side !== "rest");
  el(`${rootId}CalloutIcon`).textContent = icon;
  el(`${rootId}CalloutTitle`).textContent = title;
  el(`${rootId}CalloutDesc`).textContent = desc;
  // kind reserved if you want later
  void kind;
}

function setTotals(side, totals) {
  if (side === "rest") {
    el("restHttpTotal").textContent = `${totals.httpCalls}`;
    el("restHttpTag").textContent = totals.httpCalls > 1 ? "Multi-requêtes" : "1 requête";
    el("restDb2").textContent = totals.dbStatements != null ? `${totals.dbStatements}` : "—";
    el("restDbTag").textContent =
      totals.dbStatements != null
        ? totals.dbStatements > 2
          ? "Plusieurs requêtes"
          : "Optimisé"
        : "—";
  } else {
    el("gqlHttpTotal").textContent = `${totals.httpCalls}`;
    el("gqlHttpTag").textContent = totals.httpCalls > 1 ? "Multi-requêtes" : "1 requête";
    el("gqlDb2").textContent = totals.dbStatements != null ? `${totals.dbStatements}` : "—";
    el("gqlDbTag").textContent =
      totals.dbStatements != null
        ? totals.dbStatements > 2
          ? "N+1 possible"
          : "Avec DataLoader"
        : "—";
  }
}

function updateCompareTags({ restBytes, gqlBytes, restTimeMs, gqlTimeMs }) {
  if (Number.isFinite(restBytes) && Number.isFinite(gqlBytes)) {
    el("restSizeTag").textContent = restBytes > gqlBytes ? "Over-fetching" : "Optimisé";
    el("gqlSizeTag").textContent = gqlBytes <= restBytes ? "Optimisé" : "—";
  } else {
    el("restSizeTag").textContent = "—";
    el("gqlSizeTag").textContent = "—";
  }

  if (Number.isFinite(restTimeMs) && Number.isFinite(gqlTimeMs)) {
    el("restTimeTag").textContent = restTimeMs > gqlTimeMs ? "Plus lent" : "Rapide";
    el("gqlTimeTag").textContent = gqlTimeMs <= restTimeMs ? "Rapide" : "—";
  } else {
    el("restTimeTag").textContent = "—";
    el("gqlTimeTag").textContent = "—";
  }
}

function parseRestRequestJsonText(text) {
  const lines = text.split(/\r?\n/);
  const items = [];
  let lastLabel = null;
  let scenario = null;

  function inferScenario(label) {
    const s = (label ?? "").toUpperCase();
    if (s.includes("OVER-FETCHING") || s.includes("OVERFETCH")) return "OVER-FETCHING";
    if (s.includes("UNDER-FETCHING") || s.includes("UNDERFETCH")) return "UNDER-FETCHING";
    if (s.includes("MUTATION")) return "MUTATION";
    if (s.includes("SUBSCRIPTION") || s.includes("SUBSCRIPTION")) return "SUBSCRIPTION";
    return null;
  }

  for (const raw of lines) {
    const line = raw.trim();
    if (!line) continue;
    if (line.startsWith("#")) {
      lastLabel = line.replace(/^#+\s*/, "");
      scenario = inferScenario(lastLabel) ?? scenario;
      continue;
    }
    if (line.toLowerCase().startsWith("curl")) continue;
    if (/^https?:\/\//i.test(line)) continue; // base URL line

    const m = line.match(/^(GET|POST|PUT|DELETE|PATCH)\s+(\S+)/i);
    if (!m) continue;
    const method = m[1].toUpperCase();
    const url = m[2];
    items.push({
      label: lastLabel ? `${lastLabel}` : `${method} ${url}`,
      method,
      url,
      body: "",
      scenario: scenario ?? "CUSTOM",
    });
    lastLabel = null;
    scenario = null;
  }

  return items;
}

function parseGraphqlRequestFile(text) {
  const lines = text.split(/\r?\n/);
  const ops = [];
  let currentLabel = null;
  let buf = [];
  let inOp = false;

  function flush() {
    const q = buf.join("\n").trim();
    if (q) {
      const head = q.split(/\s+/)[0];
      const type = ["query", "mutation", "subscription"].includes(head) ? head : "query";
      const s = (currentLabel ?? "").toUpperCase();
      const scenario =
        s.includes("OVER-FETCHING") || s.includes("OVERFETCH") ? "OVER-FETCHING" :
        s.includes("UNDER-FETCHING") || s.includes("UNDERFETCH") ? "UNDER-FETCHING" :
        s.includes("MUTATION") ? "MUTATION" :
        (s.includes("SUBSCRIPTION") ? "SUBSCRIPTION" : "CUSTOM");
      ops.push({
        label: currentLabel ? `${currentLabel}` : `${type}`,
        query: q,
        type,
        scenario,
      });
    }
    currentLabel = null;
    buf = [];
    inOp = false;
  }

  for (const raw of lines) {
    const line = raw.replace(/\s+$/, "");

    if (!line.trim()) {
      if (inOp) flush();
      continue;
    }

    if (line.trim().startsWith("#")) {
      // commentaire => label si pas dans une op
      if (!inOp) currentLabel = line.trim().replace(/^#+\s*/, "");
      continue;
    }

    if (/^(query|mutation|subscription)\b/.test(line.trim())) {
      inOp = true;
    }

    if (inOp) buf.push(line);
  }

  if (inOp) flush();
  return ops;
}

function fillSelect(selectEl, items) {
  selectEl.innerHTML = "";
  const ph = document.createElement("option");
  ph.value = "";
  ph.textContent = "— choisir —";
  selectEl.appendChild(ph);

  items.forEach((it, idx) => {
    const opt = document.createElement("option");
    opt.value = String(idx);
    opt.textContent = it.label;
    selectEl.appendChild(opt);
  });
}

async function loadPresets() {
  const [restTxt, gqlTxt] = await Promise.all([
    fetch(REQUESTS_REST_TXT).then((r) => (r.ok ? r.text() : "")),
    fetch(REQUESTS_GQL_TXT).then((r) => (r.ok ? r.text() : "")),
  ]);

  const restItems = restTxt ? parseRestRequestJsonText(restTxt) : [];
  const gqlOps = gqlTxt ? parseGraphqlRequestFile(gqlTxt) : [];

  const restSelect = el("restPresetSelect");
  const gqlSelect = el("gqlPresetSelect");

  fillSelect(restSelect, restItems);
  fillSelect(gqlSelect, gqlOps);

  // le user ne choisit pas URL/méthode/body: tout est piloté par le preset
  const lockRest = () => {
    el("restUrlInput").disabled = true;
    el("restMethodInput").disabled = true;
    el("restBodyInput").disabled = true;
  };
  const lockGql = () => {
    el("gqlHttpInput").disabled = true;
    el("gqlQueryInput").disabled = true;
  };
  lockRest();
  lockGql();

  restSelect.addEventListener("change", () => {
    const idx = Number(restSelect.value);
    if (!Number.isFinite(idx)) return;
    const it = restItems[idx];
    if (!it) return;
    el("restMethodInput").value = it.method;
    el("restUrlInput").value = it.url;
    el("restBodyInput").value = it.body ?? "";

    // message côté REST selon la nature
    if (it.scenario === "OVER-FETCHING") {
      setCallout("rest", {
        kind: "warn",
        icon: "error",
        title: "Over-fetching (REST)",
        desc: "REST renvoie un format fixe (tu ne choisis pas les champs).",
      });
    } else if (it.scenario === "UNDER-FETCHING") {
      setCallout("rest", {
        kind: "warn",
        icon: "call_split",
        title: "Under-fetching (REST)",
        desc: "Pour agréger des relations, il faut plusieurs appels HTTP côté client.",
      });
    } else if (it.scenario === "MUTATION") {
      setCallout("rest", {
        kind: "mut",
        icon: "edit_square",
        title: "Mutation (REST)",
        desc: "Opération d'écriture via endpoint REST dédié.",
      });
    } else if (it.scenario === "SUBSCRIPTION") {
      setCallout("rest", {
        kind: "info",
        icon: "info",
        title: "Temps réel (REST)",
        desc: "Pas de subscription native (polling/SSE/WebSocket à implémenter).",
      });
    }
  });

  gqlSelect.addEventListener("change", () => {
    const idx = Number(gqlSelect.value);
    if (!Number.isFinite(idx)) return;
    const it = gqlOps[idx];
    if (!it) return;
    el("gqlQueryInput").value = it.query;

    if (it.scenario === "OVER-FETCHING") {
      setCallout("gql", {
        kind: "warn",
        icon: "error",
        title: "Over-fetching (GraphQL)",
        desc: "GraphQL peut aussi over-fetch si tu demandes trop de champs.",
      });
    } else if (it.scenario === "UNDER-FETCHING") {
      setCallout("gql", {
        kind: "ok",
        icon: "check_circle",
        title: "Pas d'under-fetching",
        desc: "Une seule requête GraphQL peut agréger livre + auteur + empruntsActifs.",
      });
    } else if (it.scenario === "MUTATION") {
      setCallout("gql", {
        kind: "mut",
        icon: "edit_square",
        title: "Mutation (GraphQL)",
        desc: "Mutation typée côté schéma (un seul endpoint /graphql).",
      });
    } else if (it.scenario === "SUBSCRIPTION") {
      setCallout("gql", {
        kind: "live",
        icon: "bolt",
        title: "Subscription (GraphQL)",
        desc: "Écoute temps réel via WebSocket (/graphql-ws).",
      });

      // La subscription ne passe pas via HTTP => on la lance dès la sélection
      el("gqlQuery").textContent = it.query;
      el("gqlJson").textContent = "En attente d'événements (WebSocket)…";
      setWsStatus("warn", "Relance subscription…");
      startGraphqlSubscription(it.query);
    }
  });

  // Pré-sélection: 1ère requête utile de chaque côté
  if (restItems[0]) {
    restSelect.value = "0";
    restSelect.dispatchEvent(new Event("change"));
  }
  if (gqlOps[0]) {
    gqlSelect.value = "0";
    gqlSelect.dispatchEvent(new Event("change"));
  }

  // state pour exécution synchronisée
  window.__cmp = {
    restItems,
    gqlOps,
    getRestSelected() {
      const idx = Number(restSelect.value);
      return Number.isFinite(idx) ? restItems[idx] : null;
    },
    getGqlSelected() {
      const idx = Number(gqlSelect.value);
      return Number.isFinite(idx) ? gqlOps[idx] : null;
    },
  };
}

async function runRestOnce({ url, method, body }) {
  el("restEndpointLabel").textContent = url.replace(location.origin, "");
  el("restHint").textContent = method === "GET" ? "Récupère des champs (REST fixe)" : "Mutation REST";

  el("restJson").textContent = "Chargement…";

  const opts = { method };
  if (body && method !== "GET") {
    opts.headers = { "content-type": "application/json" };
    opts.body = body;
  }

  const r = await timedFetch(url, opts);
  const m = headersToMetrics(r.headers);

  if (!r.ok) {
    el("restJson").textContent = `Erreur REST ${r.status}\n${r.text}`;
    el("restTime").textContent = `${r.timeMs} ms`;
    el("restTime2").textContent = `${r.timeMs} ms`;
    setCallout("rest", {
      kind: "bad",
      icon: "error",
      title: "Erreur REST",
      desc: `HTTP ${r.status}`,
    });
    return { httpCalls: 1, bytes: r.bytes, timeMs: r.timeMs, dbStatements: m.dbStatements };
  }

  const arr = Array.isArray(r.json) ? r.json : null;
  const first = arr ? arr[0] : r.json;
  const fields = approxFieldCount(first);

  el("restJson").textContent = prettyJson(first);
  el("restFieldsMeta").textContent = `~${fields} champs retournés`;
  el("restSize").textContent = formatBytes(r.bytes);
  el("restSizeMeta").textContent = arr ? `~${fields} champs (extrait 1/${arr.length})` : `~${fields} champs`;
  el("restTime").textContent = `${r.timeMs} ms`;
  el("restHttpCalls").textContent = "1 requête HTTP";

  el("restDb").textContent = m.dbStatements != null ? `${m.dbStatements}` : "—";
  el("restInternal").textContent =
    m.internalCalls != null ? `${m.internalCalls} appels internes` : "—";

  el("restSize2").textContent = formatBytes(r.bytes);
  el("restTime2").textContent = `${r.timeMs} ms`;

  setCallout("rest", {
    kind: "warn",
    icon: "error",
    title: "Over-fetching possible",
    desc: "REST renvoie un format fixe (tu ne choisis pas les champs).",
  });

  return { httpCalls: 1, bytes: r.bytes, timeMs: r.timeMs, dbStatements: m.dbStatements };
}

async function runRestMutationBorrowFirstAvailable(adherentId = 2) {
  // 1) Prendre un livre disponible
  const books = await timedFetch("/api/livres?disponible=true", { method: "GET" });
  if (!books.ok || !Array.isArray(books.json) || books.json.length === 0) {
    el("restJson").textContent = `Impossible de trouver un livre disponible\nHTTP ${books.status}\n${books.text}`;
    setCallout("rest", { kind: "bad", icon: "error", title: "Mutation (REST)", desc: "Aucun livre disponible." });
    return { httpCalls: 1, bytes: books.bytes, timeMs: books.timeMs, dbStatements: null };
  }

  const livreId = books.json[0].id;
  const url = `/api/livres/${encodeURIComponent(livreId)}/emprunter?adherentId=${encodeURIComponent(adherentId)}`;
  el("restEndpointLabel").textContent = url;
  el("restHint").textContent = "Mutation REST (livre disponible auto)";

  // 2) Emprunter
  const r = await runRestOnce({ url, method: "POST", body: "" });
  el("restHttpCalls").textContent = "2 requêtes HTTP (pré-check + mutation)";
  setTotals("rest", { httpCalls: 2, dbStatements: null });
  return { ...r, httpCalls: 2 };
}

async function runGqlOnce({ endpoint, query }) {
  el("gqlEndpointLabel").textContent = endpoint.replace(location.origin, "");
  el("gqlHint").textContent = "Sélectionne uniquement les champs nécessaires";
  el("gqlQuery").textContent = query;
  el("gqlJson").textContent = "Chargement…";

  const r = await timedFetch(endpoint, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ query }),
  });
  const m = headersToMetrics(r.headers);

  if (!r.ok) {
    el("gqlJson").textContent = `Erreur GraphQL HTTP ${r.status}\n${r.text}`;
    el("gqlTime").textContent = `${r.timeMs} ms`;
    el("gqlTime2").textContent = `${r.timeMs} ms`;
    setCallout("gql", {
      kind: "bad",
      icon: "cancel",
      title: "Erreur GraphQL",
      desc: `HTTP ${r.status}`,
    });
    return { httpCalls: 1, bytes: r.bytes, timeMs: r.timeMs, dbStatements: m.dbStatements };
  }

  const firstItem =
    Array.isArray(r.json?.data?.livres) ? r.json.data.livres[0] : (r.json?.data ?? null);
  const fields = approxFieldCount(firstItem);

  el("gqlJson").textContent = prettyJson(r.json?.data ?? r.json);
  el("gqlFieldsMeta").textContent = `~${fields} champs retournés`;

  el("gqlSize").textContent = formatBytes(r.bytes);
  el("gqlSizeMeta").textContent = `~${fields} champs (selon sélection)`;
  el("gqlTime").textContent = `${r.timeMs} ms`;
  el("gqlHttpCalls").textContent = "1 requête HTTP";

  el("gqlDb").textContent = m.dbStatements != null ? `${m.dbStatements}` : "—";
  el("gqlInternal").textContent =
    m.internalCalls != null ? `${m.internalCalls} appels internes` : "—";

  el("gqlSize2").textContent = formatBytes(r.bytes);
  el("gqlTime2").textContent = `${r.timeMs} ms`;

  setCallout("gql", {
    kind: "ok",
    icon: "check_circle",
    title: "Pas d'over-fetching",
    desc: "Tu récupères exactement les champs demandés dans la requête.",
  });

  return { httpCalls: 1, bytes: r.bytes, timeMs: r.timeMs, dbStatements: m.dbStatements };
}

async function runAggRest() {
  // Stratégie volontairement "under-fetching": 1 appel livres + N appels emprunts actifs par livre
  const base = "/api/livres";
  const books = await timedFetch(base, { method: "GET" });
  const m0 = headersToMetrics(books.headers);
  if (!books.ok || !Array.isArray(books.json)) {
    el("aggOutput").textContent = `REST agrégé: erreur\nHTTP ${books.status}\n${books.text}`;
    return;
  }

  const livres = books.json.slice(0, 8); // pour garder un scénario lisible
  let httpCalls = 1;
  let bytes = books.bytes;
  let timeMs = books.timeMs;
  let dbStatements = (m0.dbStatements ?? 0);

  const actifsByLivre = {};
  for (const l of livres) {
    const r = await timedFetch(`/api/emprunts?livreId=${encodeURIComponent(l.id)}&actifs=true`, {
      method: "GET",
    });
    const mi = headersToMetrics(r.headers);
    httpCalls += 1;
    bytes += r.bytes;
    timeMs += r.timeMs;
    if (mi.dbStatements != null) dbStatements += mi.dbStatements;
    actifsByLivre[l.id] = Array.isArray(r.json) ? r.json : [];
  }

  const aggregated = livres.map((l) => ({
    id: l.id,
    titre: l.titre,
    auteur: l.auteur ?? null,
    empruntsActifs: actifsByLivre[l.id].map((e) => ({
      id: e.id,
      dateRetourPrevue: e.dateRetourPrevue,
      adherent: e.adherent ? { id: e.adherent.id, nom: e.adherent.nom } : null,
    })),
  }));

  el("aggOutput").textContent =
    `REST (agrégé côté client)\n` +
    `- appels HTTP: ${httpCalls}\n` +
    `- taille totale: ${formatBytes(bytes)}\n` +
    `- temps total (somme): ${timeMs} ms\n` +
    `- requêtes DB (somme headers): ${dbStatements}\n\n` +
    prettyJson({ livres: aggregated });

  setTotals("rest", { httpCalls, dbStatements });
  el("restHttpCalls").textContent = `${httpCalls} requêtes HTTP (agrégé)`;
}

async function runAggGql() {
  const query = `query {
  livres {
    id
    titre
    auteur { id nom nationalite }
    empruntsActifs { id dateRetourPrevue adherent { id nom } }
  }
}`;

  const r = await runGqlOnce({ endpoint: el("gqlHttpInput").value, query });
  el("aggOutput").textContent =
    `GraphQL (agrégé nativement)\n` +
    `- appels HTTP: ${r.httpCalls}\n` +
    `- taille: ${formatBytes(r.bytes)}\n` +
    `- temps: ${r.timeMs} ms\n` +
    `- requêtes DB (header): ${r.dbStatements ?? "—"}\n\n` +
    el("gqlJson").textContent;

  setTotals("gql", { httpCalls: 1, dbStatements: r.dbStatements });
  el("gqlHttpCalls").textContent = "1 requête HTTP (agrégé)";
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
  while (wrap.children.length > 10) wrap.removeChild(wrap.lastElementChild);
}

function setWsStatus(kind, text) {
  const node = el("wsStatus");
  node.textContent = text;
  node.classList.remove("status__value--ok", "status__value--warn", "status__value--bad");
  if (kind === "ok") node.classList.add("status__value--ok");
  if (kind === "warn") node.classList.add("status__value--warn");
  if (kind === "bad") node.classList.add("status__value--bad");
}

let __gqlWs = null;

function startGraphqlSubscription(queryText) {
  const livePill = el("livePill");

  // éviter d'empiler des websockets si l'utilisateur relance la subscription
  try {
    __gqlWs?.close?.(1000, "restart");
  } catch {}
  __gqlWs = null;

  let ws;
  try {
    ws = new WebSocket(GRAPHQL_WS_URL, "graphql-transport-ws");
  } catch {
    setWsStatus("bad", "WebSocket indisponible");
    livePill.classList.add("off");
    return;
  }
  __gqlWs = ws;

  let acknowledged = false;
  const subQuery =
    (queryText && String(queryText).trim()) ||
    `subscription($genre: Genre) {
  livreDisponible(genre: $genre) {
    titre
    auteur { nom }
  }
}`;

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
          id: "sub-1",
          type: "subscribe",
          // si la requête contient $genre, on fournit la variable par défaut
          payload: {
            query: subQuery,
            variables: subQuery.includes("$genre") ? { genre: "ROMAN" } : {},
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

function openDrawer() {
  el("drawer").hidden = false;
  el("drawerBackdrop").hidden = false;
}
function closeDrawer() {
  el("drawer").hidden = true;
  el("drawerBackdrop").hidden = true;
}
function isDrawerOpen() {
  return !el("drawer").hidden;
}

function initLabDefaults() {
  el("restUrlInput").value = DEFAULTS.restUrl;
  el("restMethodInput").value = DEFAULTS.restMethod;
  el("restBodyInput").value = DEFAULTS.restBody;
  el("gqlHttpInput").value = DEFAULTS.gqlHttp;
  el("gqlQueryInput").value = DEFAULTS.gqlQuery;
}

function resolveUrl(input) {
  // accepte une URL absolue ou relative (relative => same origin REST)
  try {
    return new URL(input, location.origin).toString();
  } catch {
    return input;
  }
}

async function runGqlMutationBorrowFirstAvailable(adherentId = 10) {
  const endpoint = el("gqlHttpInput").value.trim();

  // 1) Trouver un livre disponible
  const q1 = `query {
  livres(disponible: true) { id titre disponible }
}`;
  const r1 = await timedFetch(endpoint, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ query: q1 }),
  });
  const livres = r1.json?.data?.livres;
  const first = Array.isArray(livres) ? livres.find((l) => l && l.disponible) : null;
  if (!first?.id) {
    el("gqlQuery").textContent = q1;
    el("gqlJson").textContent = prettyJson(r1.json ?? r1.text);
    setCallout("gql", { kind: "bad", icon: "cancel", title: "Mutation (GraphQL)", desc: "Aucun livre disponible." });
    return;
  }

  // 2) Muter
  const q2 = `mutation {
  emprunterLivre(livreId: ${Number(first.id)}, adherentId: ${Number(adherentId)}) {
    id
    dateRetourPrevue
    livre { titre }
  }
}`;
  el("gqlQueryInput").value = q2;
  await runGqlOnce({ endpoint, query: q2 });
  el("gqlHttpCalls").textContent = "2 requêtes HTTP (pré-check + mutation)";
  setTotals("gql", { httpCalls: 2, dbStatements: null });
}

async function wire() {
  // Toujours fermé par défaut (même si cache/état DOM bizarre)
  closeDrawer();
  initLabDefaults();

  el("openLab").addEventListener("click", () => {
    if (isDrawerOpen()) closeDrawer();
    else openDrawer();
  });
  el("closeLab").addEventListener("click", closeDrawer);
  el("drawerBackdrop").addEventListener("click", closeDrawer);
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && isDrawerOpen()) closeDrawer();
  });

  // Charger les presets depuis les fichiers du repo
  loadPresets().catch(() => {
    // pas bloquant: labo reste editable
    const rs = el("restPresetSelect");
    const gs = el("gqlPresetSelect");
    if (rs) rs.innerHTML = "<option value=''>Impossible de charger</option>";
    if (gs) gs.innerHTML = "<option value=''>Impossible de charger</option>";
  });

  el("runRestBtn").addEventListener("click", async () => {
    const selected = window.__cmp?.getRestSelected?.();
    if (selected?.scenario === "UNDER-FETCHING") {
      await runAggRest();
      return;
    }
    if (selected?.scenario === "MUTATION") {
      await runRestMutationBorrowFirstAvailable(2);
      return;
    }
    const url = resolveUrl(el("restUrlInput").value.trim());
    const method = el("restMethodInput").value;
    const body = el("restBodyInput").value.trim();
    const r = await runRestOnce({ url, method, body });
    setTotals("rest", { httpCalls: 1, dbStatements: r.dbStatements });
  });

  el("runGqlBtn").addEventListener("click", async () => {
    const selected = window.__cmp?.getGqlSelected?.();
    if (selected?.scenario === "SUBSCRIPTION") {
      // la subscription est pilotée via WebSocket (section monitoring)
      setWsStatus("warn", "Relance subscription…");
      startGraphqlSubscription();
      return;
    }
    if (selected?.scenario === "MUTATION") {
      await runGqlMutationBorrowFirstAvailable(10);
      return;
    }
    const endpoint = el("gqlHttpInput").value.trim();
    const query = el("gqlQueryInput").value.trim();
    const r = await runGqlOnce({ endpoint, query });
    setTotals("gql", { httpCalls: 1, dbStatements: r.dbStatements });
  });

  el("runAggRestBtn").addEventListener("click", runAggRest);
  el("runAggGqlBtn").addEventListener("click", runAggGql);

  // auto start: subscription only (messages vrais)
  setCallout("rest", {
    kind: "muted",
    icon: "error",
    title: "En attente",
    desc: "Ouvre le labo et exécute une requête REST.",
  });
  setCallout("gql", {
    kind: "muted",
    icon: "check_circle",
    title: "En attente",
    desc: "Ouvre le labo et exécute une requête GraphQL.",
  });

  setTotals("rest", { httpCalls: 0, dbStatements: null });
  setTotals("gql", { httpCalls: 0, dbStatements: null });
  el("restSizeTag").textContent = "—";
  el("gqlSizeTag").textContent = "—";
  el("restTimeTag").textContent = "—";
  el("gqlTimeTag").textContent = "—";

  startGraphqlSubscription();
}

wire();

