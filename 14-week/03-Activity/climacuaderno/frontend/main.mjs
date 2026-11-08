import { apiRequest, describeError } from './api.mjs';

const get = (id) => document.getElementById(id);
const state = { token: '', email: '', readings: [], mode: 'login' };

function message(element, text, kind = '') {
  element.textContent = text;
  element.className = `message ${kind}`;
}

function setScreen(loggedIn) {
  get('auth-screen').hidden = loggedIn;
  get('dashboard').hidden = !loggedIn;
  get('logout-button').hidden = !loggedIn;
}

function signOut(note = '') {
  state.token = '';
  state.email = '';
  state.readings = [];
  setScreen(false);
  get('password-input').value = '';
  if (note) message(get('auth-message'), note, 'error');
}

function switchMode(mode) {
  state.mode = mode;
  const register = mode === 'register';
  get('auth-title').textContent = register ? 'Start a new journal.' : 'Welcome back.';
  get('auth-description').textContent = register ? 'Create a personal space for your field measurements.' : 'Sign in to see your field readings.';
  get('auth-submit').firstChild.textContent = register ? 'Create account ' : 'Sign in ';
  get('mode-question').textContent = register ? 'Already have a journal?' : 'New to this journal?';
  get('mode-button').textContent = register ? 'Sign in' : 'Create account';
  get('password-input').autocomplete = register ? 'new-password' : 'current-password';
  message(get('auth-message'), '');
}

get('mode-button').addEventListener('click', () => switchMode(state.mode === 'login' ? 'register' : 'login'));
get('logout-button').addEventListener('click', () => signOut());

get('auth-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const email = get('email-input').value.trim();
  const password = get('password-input').value;
  const submit = get('auth-submit');
  if (!email || !password) {
    message(get('auth-message'), 'Email and password are required.', 'error');
    return;
  }
  if (state.mode === 'register' && password.length < 10) {
    message(get('auth-message'), 'Use a password with at least 10 characters.', 'error');
    return;
  }
  submit.disabled = true;
  message(get('auth-message'), 'Connecting to your private journal…');
  try {
    if (state.mode === 'register') {
      await apiRequest('/auth/register', { method: 'POST', body: { email, password } });
    }
    const auth = await apiRequest('/auth/login', { method: 'POST', body: { email, password } });
    state.token = auth.access_token;
    const profile = await apiRequest('/me', { token: state.token });
    state.email = profile.email;
    get('account-label').textContent = state.email;
    setScreen(true);
    message(get('auth-message'), '');
    await loadReadings();
  } catch (error) {
    state.token = '';
    message(get('auth-message'), error.status === 401 ? 'Incorrect email or password.' : describeError(error), 'error');
  } finally {
    submit.disabled = false;
  }
});

function updateSummary() {
  get('total-stat').textContent = state.readings.length;
  get('balanced-stat').textContent = state.readings.filter((item) => item.assessment === 'balanced').length;
  get('review-stat').textContent = state.readings.filter((item) => item.assessment !== 'balanced').length;
}

function cell(text, className = '') {
  const element = document.createElement('td');
  element.textContent = String(text);
  if (className) element.className = className;
  return element;
}

function renderReadings() {
  const table = get('readings-table');
  table.replaceChildren();
  updateSummary();
  get('empty-message').hidden = state.readings.length > 0;
  for (const item of state.readings) {
    const row = document.createElement('tr');
    const title = document.createElement('td');
    const zone = document.createElement('strong');
    zone.textContent = item.zone_name;
    const date = document.createElement('small');
    date.textContent = new Date(item.recorded_at).toLocaleString();
    title.append(zone, date);
    row.append(title, cell(`${item.temperature.toFixed(1)}°`), cell(`${item.humidity.toFixed(1)}%`));
    const assessment = document.createElement('td');
    const tag = document.createElement('span');
    tag.className = `status-tag ${item.assessment}`;
    tag.textContent = item.assessment === 'balanced' ? 'Balanced' : 'Review';
    assessment.append(tag);
    row.append(assessment);
    const actions = document.createElement('td');
    actions.className = 'action-cell';
    const remove = document.createElement('button');
    remove.className = 'delete-button';
    remove.type = 'button';
    remove.textContent = 'Remove';
    remove.setAttribute('aria-label', `Remove reading from ${item.zone_name}`);
    remove.addEventListener('click', () => deleteReading(item.id, remove));
    actions.append(remove);
    row.append(actions);
    table.append(row);
  }
}

async function loadReadings() {
  get('loading-message').textContent = 'Loading your saved observations…';
  get('refresh-button').disabled = true;
  try {
    state.readings = await apiRequest('/readings', { token: state.token });
    renderReadings();
    get('loading-message').textContent = `${state.readings.length} reading(s) available.`;
  } catch (error) {
    if (error.status === 401) return signOut('Your session expired. Please sign in again.');
    get('loading-message').textContent = describeError(error);
  } finally {
    get('refresh-button').disabled = false;
  }
}

async function deleteReading(id, button) {
  button.disabled = true;
  try {
    await apiRequest(`/readings/${id}`, { method: 'DELETE', token: state.token });
    await loadReadings();
  } catch (error) {
    if (error.status === 401) return signOut('Your session expired. Please sign in again.');
    get('loading-message').textContent = describeError(error);
    button.disabled = false;
  }
}

get('refresh-button').addEventListener('click', loadReadings);
get('reading-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const submit = get('reading-submit');
  submit.disabled = true;
  message(get('reading-message'), 'Saving observation…');
  const body = {
    zone_name: get('zone-input').value.trim(),
    temperature: Number(get('temperature-input').value),
    humidity: Number(get('humidity-input').value),
  };
  try {
    await apiRequest('/readings', { method: 'POST', token: state.token, body });
    get('reading-form').reset();
    message(get('reading-message'), 'Observation recorded successfully.', 'success');
    await loadReadings();
  } catch (error) {
    if (error.status === 401) return signOut('Your session expired. Please sign in again.');
    message(get('reading-message'), describeError(error), 'error');
  } finally {
    submit.disabled = false;
  }
});

setScreen(false);
