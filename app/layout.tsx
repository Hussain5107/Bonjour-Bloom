import type { Metadata } from 'next';
import './globals.css';
import { Providers } from './providers';

export const metadata: Metadata = {
  title: 'Bonjour Bloom — French for curious kids',
  description: 'Gentle, joyful French lessons that grow with your child.',
  manifest: '/manifest.webmanifest',
  appleWebApp: { capable:true, statusBarStyle:'default', title:'Bonjour Bloom' },
  icons: { icon:'/milo-icon.png', apple:'/milo-icon.png' },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body><Providers>{children}</Providers></body>
    </html>
  );
}
