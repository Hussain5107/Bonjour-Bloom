import fs from 'node:fs';
import {describe,expect,it} from 'vitest';

const app=fs.readFileSync(new URL('../app/bloom-app.tsx',import.meta.url),'utf8');
const css=fs.readFileSync(new URL('../app/responsive-phase1.css',import.meta.url),'utf8');

describe('responsive application contract',()=>{
 it('shares five primary destinations between desktop and mobile navigation',()=>{
  for(const destination of ['home','learn','review','progress','profile'])expect(app).toContain(`id:'${destination}'`);
  expect(app).toContain('<AppNavigation view={view}');
  expect(app).toContain('<AppNavigation mobile view={view}');
  expect(app).toContain('aria-current={view===id');
 });
 it('keeps bottom navigation through tablet widths and restores the sidebar at desktop',()=>{
  expect(css).toContain('@media (max-width:1023px)');
  expect(css).toContain('.sidebar{display:none!important}');
  expect(css).toContain('grid-template-columns:repeat(5,minmax(0,1fr))');
  expect(css).toContain('@media (min-width:1024px)');
 });
 it('accounts for installed-app safe areas and mobile lesson actions',()=>{
  expect(css).toContain('env(safe-area-inset-bottom)');
  expect(css).toContain('env(safe-area-inset-top)');
  expect(css).toContain('.lesson-screen>footer');
  expect(css).toContain('min-height:44px');
 });
});
