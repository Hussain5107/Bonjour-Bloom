import {readFileSync} from 'node:fs';
import {describe,expect,it} from 'vitest';

describe('mobile shell centering',()=>{
 const css=readFileSync('app/globals.css','utf8');
 it('locks the shell and header to the viewport',()=>{
  expect(css).toContain('body .app-shell { inline-size: 100%; max-inline-size: none');
  expect(css).toContain('body .topbar { inset-inline: 0; inline-size: auto');
  expect(css).not.toMatch(/overflow-x:\s*(hidden|clip)/);
  expect(css).not.toContain('max-inline-size: 100vw');
 });
 it('centers main content and the profile summary',()=>{
  expect(css).toContain('margin-inline: auto');
  expect(css).toContain('body .profile-context { inline-size: calc(100% - 24px); margin: 8px auto 0; }');
 });
});
