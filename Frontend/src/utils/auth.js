/**
 * auth.js - Tab-isolated session management
 *
 * Strategy:
 *   - sessionStorage = per-tab (tab A & tab B are independent)
 *   - localStorage   = persistent login (fallback / remembered login)
 *
 * On login -> save to sessionStorage (this tab only)
 * On read  -> prefer sessionStorage, fallback localStorage
 * On logout -> clear sessionStorage only (other tabs unaffected)
 */

const SESSION_KEYS = ["user", "token", "userRole"];

/** Save auth data to THIS TAB only (sessionStorage) */
export function saveSession(user, token, role) {
  if (user !== undefined) {
    const value = typeof user === "string" ? user : JSON.stringify(user);
    sessionStorage.setItem("user", value);
  }
  if (token !== undefined && token !== null) {
    sessionStorage.setItem("token", token);
  }
  if (role !== undefined) {
    sessionStorage.setItem("userRole", role);
  }
}

/** Read a session value: prefer sessionStorage, fallback localStorage */
export function getSession(key) {
  return sessionStorage.getItem(key) ?? localStorage.getItem(key) ?? null;
}

/** Remove auth data from THIS TAB and localStorage */
export function clearSession() {
  SESSION_KEYS.forEach((key) => {
    sessionStorage.removeItem(key);
    localStorage.removeItem(key);
  });
}

/** Check if current tab has an active session */
export function isLoggedIn() {
  return !!(sessionStorage.getItem("token") || getSession("token"));
}

/** Get parsed user object */
export function getUser() {
  try {
    const raw = getSession("user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

/** Get role string */
export function getUserRole() {
  return getSession("userRole");
}

/** Get token string */
export function getToken() {
  return getSession("token");
}