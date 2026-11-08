import test from 'node:test';
import assert from 'node:assert/strict';
import { ApiError, apiRequest, describeError } from '../api.mjs';

test('adds the bearer token to a private request', async () => {
  let sentHeaders;
  const transport = async (url, options) => {
    assert.equal(url, '/api/readings');
    sentHeaders = options.headers;
    return { ok: true, status: 200, json: async () => [{ id: 1 }] };
  };
  const data = await apiRequest('/readings', { token: 'example-jwt-value' }, transport);
  assert.equal(sentHeaders.Authorization, 'Bearer example-jwt-value');
  assert.equal(data.length, 1);
});

test('converts 401 into a predictable error state', async () => {
  const transport = async () => ({
    ok: false,
    status: 401,
    json: async () => ({ detail: 'Invalid or expired session' }),
  });
  await assert.rejects(() => apiRequest('/readings', { token: 'expired' }, transport), (error) => {
    assert.equal(error.status, 401);
    assert.match(describeError(error), /sign in again/i);
    return true;
  });
});

test('network loss returns a user-readable message', async () => {
  const offline = async () => { throw new TypeError('Failed to fetch'); };
  await assert.rejects(() => apiRequest('/readings', {}, offline), (error) => {
    assert.ok(error instanceof ApiError);
    assert.equal(error.status, 0);
    assert.match(describeError(error), /network/i);
    return true;
  });
});

test('sends JSON without adding a token for public login', async () => {
  let options;
  const transport = async (_url, settings) => {
    options = settings;
    return { ok: true, status: 200, json: async () => ({ access_token: 'issued' }) };
  };
  await apiRequest('/auth/login', { method: 'POST', body: { email: 'a@b.co', password: 'secret' } }, transport);
  assert.equal(options.headers.Authorization, undefined);
  assert.equal(options.headers['Content-Type'], 'application/json');
  assert.equal(JSON.parse(options.body).email, 'a@b.co');
});
