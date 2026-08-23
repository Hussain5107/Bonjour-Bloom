import {readFileSync} from 'node:fs';
import {describe,expect,it} from 'vitest';

describe('mobile shell centering',()=>{
 const css=readFileSync('app/mobile-centering.css','utf8');
 it('locks the shell and header to the viewport',()=>{
  expect(css).toContain('.app-shell { inline-size: 100%; max-inline-size: 100vw');
  expect(css).toContain('.topbar { inset-inline: 0; inline-size: 100%');
 });
 it('centers main content and the profile summary',()=>{
  expect(css).toContain('margin-inline: auto');
  expect(css).toContain('.profile-context { inline-size: calc(100% - 24px); margin: 8px auto 0; }');
 });
});
