import type {Metadata} from 'next';
import './globals.css';
import {Providers} from './providers';
import {AccountGate} from './account-gate';
import {ProfileCloudSync} from './profile-cloud-sync';

export const metadata:Metadata={title:'Bonjour Bloom — French for curious learners',description:'Gentle, personalized French lessons for individual learners and families.',manifest:'/manifest.webmanifest',appleWebApp:{capable:true,statusBarStyle:'default',title:'Bonjour Bloom'},icons:{icon:'/milo-icon.png',apple:'/milo-icon.png'}};
export default function RootLayout({children}:{children:React.ReactNode}){return <html lang="en"><body><Providers><AccountGate><ProfileCloudSync/>{children}</AccountGate></Providers></body></html>}
