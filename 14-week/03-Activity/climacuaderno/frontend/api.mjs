/** Small, dependency-free client. Unit tested with Node's built-in test runner. */
export class ApiError extends Error {
  constructor(message, status = 0) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

export async function apiRequest(path, { method = 'GET', token = '', body } = {}, transport = fetch) {
  const headers = { Accept: 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  let response;
  try {
    response = await transport(`/api${path}`, {
      method,
      headers,
      credentials: 'omit',
      ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
    });
  } catch {
    throw new ApiError('Connection unavailable. Check your network and try again.', 0);
  }
  const payload = response.status === 204 ? null : await response.json().catch(() => null);
  if (!response.ok) {
    const detail = typeof payload?.detail === 'string' ? payload.detail : 'Request failed.';
    throw new ApiError(detail, response.status);
  }
  return payload;
}

export function describeError(error) {
  if (error instanceof ApiError && error.status === 401) return 'Access denied or session expired. Please sign in again.';
  return error instanceof Error ? error.message : 'An unexpected error occurred.';
}
