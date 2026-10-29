export const API_BASE = "http://localhost:8080";

export async function request(path, options={}) {
  let res;
  try {
    res = await fetch(API_BASE + path, {
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
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
