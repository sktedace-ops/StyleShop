export const API_BASE = "http://localhost:8080";
const TOKEN_KEY = "styleshop.accessToken";

export function getAccessToken() {
  return sessionStorage.getItem(TOKEN_KEY) || "";
}

export function setAccessToken(token) {
  if (token) sessionStorage.setItem(TOKEN_KEY, token);
  else sessionStorage.removeItem(TOKEN_KEY);
}

export async function request(path, options={}) {
  let res;
  try {
    res = await fetch(API_BASE + path, {
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        ...(getAccessToken() ? {Authorization: `Bearer ${getAccessToken()}`} : {}),
        ...(options.headers || {})
      },
      ...options
    });
  } catch (err) {
    const e = new Error("無法連線到 Spring Boot 後端，請確認 http://localhost:8080 已啟動");
    e.status = 0;
    throw e;
  }

  let data = null;
  try {
    data = await res.json();
  } catch {}

  if (!res.ok) {
    if (res.status === 401) setAccessToken("");
    const e = new Error(data?.message || `操作失敗（HTTP ${res.status}）`);
    e.status = res.status;
    throw e;
  }

  return data;
}

export function imageUrl(path) {
  if (!path) return API_BASE + "/images/no-image.svg";
  if (path.startsWith("http://") || path.startsWith("https://")) return path;
  return API_BASE + path;
}

export async function downloadBase64Pdf(path) {
  const data = await request(path);
  const binary = atob(data.base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  const url = URL.createObjectURL(new Blob([bytes], {type: data.contentType || "application/pdf"}));
  const link = document.createElement("a");
  link.href = url;
  link.download = data.fileName || "report.pdf";
  link.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}
