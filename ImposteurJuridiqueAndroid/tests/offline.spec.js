const { test, expect } = require('@playwright/test');
const { pathToFileURL } = require('url');
const path = require('path');
const url = pathToFileURL(path.resolve('app/src/main/assets/index.html')).href;

async function openOffline(page, context) {
  await context.setOffline(true);
  const errors = [];
  const remote = [];
  page.on('pageerror', e => errors.push(e.message));
  page.on('request', r => { if (/^https?:/.test(r.url())) remote.push(r.url()); });
  await page.goto(url);
  await page.evaluate(() => document.fonts.ready);
  return { errors, remote };
}

test('Chargement hors ligne : police originale, icônes et interrupteur', async ({page, context}) => {
  const report = await openOffline(page, context);
  expect(await page.evaluate(() => document.fonts.check('400 12px "Plus Jakarta Sans"'))).toBe(true);
  expect(await page.evaluate(() => document.fonts.check('900 12px "Font Awesome 6 Free"'))).toBe(true);
  const h = await page.locator('#rule-hint-toggle + div').evaluate(el => el.getBoundingClientRect().height);
  expect(h).toBe(22);
  expect(report.errors).toEqual([]);
  expect(report.remote).toEqual([]);
});

test('Partie complète, appui maintenu et masquage immédiat', async ({page, context}) => {
  const report = await openOffline(page, context);
  await page.locator('#main-action-btn').click();
  for (let i=0;i<4;i++) {
    await expect(page.locator('#step-prepare')).toBeVisible();
    await expect(page.locator('#role-card-container')).toHaveAttribute('aria-hidden', 'true');
    const box = await page.locator('#fingerprint-btn').boundingBox();
    await page.mouse.move(box.x+box.width/2, box.y+box.height/2);
    await page.mouse.down();
    await expect(page.locator('#role-card-container')).toHaveAttribute('aria-hidden', 'false');
    await page.mouse.up();
    await expect(page.locator('#reveal-word')).toBeHidden();
    await page.locator('#main-action-btn').click();
  }
  await expect(page.locator('#step-discussion')).toBeVisible();
  await page.locator('#main-action-btn').click();
  await expect(page.locator('#step-verdict')).toBeVisible();
  const names = await page.evaluate(() => state.impostorIndexes.map(i => state.players[i]).join(', '));
  await expect(page.locator('#verdict-impostor-name')).toHaveText(names);
  await page.locator('#main-action-btn').click();
  await expect(page.locator('#step-config')).toBeVisible();
  expect(report.errors).toEqual([]); expect(report.remote).toEqual([]);
});

test('Limites joueurs, injection neutralisée et réglages conservés', async ({page, context}) => {
  await openOffline(page, context);
  page.on('dialog', d => d.accept());
  await page.evaluate(() => openPlayersModal());
  await page.locator('#new-player-input').fill('<img src=x onerror=alert(1)>');
  await page.evaluate(() => addPlayer());
  await expect(page.locator('#players-list-container img')).toHaveCount(0);
  await expect(page.locator('#players-list-container')).toContainText('<img src=x onerror=alert(1)>');
  await page.evaluate(() => { for(let i=0;i<8;i++){ document.getElementById('new-player-input').value='Joueur '+i; addPlayer(); } });
  expect(await page.evaluate(() => state.players.length)).toBe(10);
  await page.evaluate(() => { while(state.players.length>3) removePlayer(0); removePlayer(0); });
  expect(await page.evaluate(() => state.players.length)).toBe(3);
  await page.evaluate(() => toggleImpostorCount());
  expect(await page.evaluate(() => state.impostorCount)).toBe(1);
  await page.reload();
  expect(await page.evaluate(() => state.players.length)).toBe(3);
  expect(await page.evaluate(() => state.currentStep)).toBe('config');
});

test('Catégorie minimale toujours cochée ; deux imposteurs avec cinq joueurs', async ({page, context}) => {
  await openOffline(page, context);
  await page.evaluate(() => { for (const c of [...state.categories]) toggleCategory(c); openCategoriesModal(); });
  expect(await page.locator('#categories-list-container input:checked').count()).toBe(1);
  expect(await page.evaluate(() => state.categories.length)).toBe(1);
  await page.evaluate(() => { closeModal('modal-categories'); document.getElementById('new-player-input').value='Cinquième'; addPlayer(); toggleImpostorCount(); });
  expect(await page.evaluate(() => state.impostorCount)).toBe(2);
  await page.locator('#rule-hint-toggle').uncheck({force:true});
  await page.locator('#main-action-btn').click();
  expect(await page.evaluate(() => state.impostorIndexes.length)).toBe(2);
  await page.evaluate(() => { state.currentPlayerIndex=state.impostorIndexes[0]; switchStep('prepare'); showSecret(); });
  await expect(page.locator('#reveal-definition-box')).toContainText("Vous n'avez pas le mot secret");
  await page.evaluate(() => window.dispatchEvent(new Event('blur')));
  await expect(page.locator('#reveal-word')).toBeHidden();
  await page.evaluate(() => { showSecret(); document.getElementById('fingerprint-btn').dispatchEvent(new PointerEvent('pointercancel')); });
  await expect(page.locator('#reveal-word')).toBeHidden();
  await page.evaluate(() => { openRulesModal(); handleAndroidBack(); });
  await expect(page.locator('#modal-rules')).toBeHidden();
});

for (const [width,height] of [[320,568],[360,720],[393,852],[412,915],[640,360],[800,1280]]) {
 test(`Écrans accessibles ${width}×${height}`, async ({page, context}) => {
  await page.setViewportSize({width,height});
  await openOffline(page, context);
  for(const step of ['config','prepare','discussion','verdict']) {
   await page.evaluate(step => { if(step==='prepare') startNewGame(); else switchStep(step); },step);
   expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
   const box = await page.locator('#main-action-btn').boundingBox();
   expect(box.y).toBeGreaterThanOrEqual(0);
   expect(box.y+box.height).toBeLessThanOrEqual(height);
  }
 });
}
