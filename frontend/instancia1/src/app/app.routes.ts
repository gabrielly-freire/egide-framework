import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Layout } from './layout/layout';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';
import { landingGuard, roleGuard } from './guards/role-guard';

export const routes: Routes = [
  {
    path: 'login',
    component: Login,
    title: 'Egide - Login'
  },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: Home,
        canActivate: [landingGuard]
      },
      {
        path: 'home',
        component: Home,
        title: 'Égide - Início'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.Dashboard),
        title: 'Égide - Dashboard',
        canActivate: [roleGuard],
        data: { roles: ['LISTENER', 'GENERAL_LISTENER', 'MANAGER', 'ADMIN'] }
      },
      {
        path: 'cadastrar-manifestacao',
        loadComponent: () =>
          import('./pages/report-registration/report-registration').then(m => m.ReportRegistration),
        title: 'Égide - Nova Manifestação',
        canActivate: [roleGuard],
        data: { roles: ['REMONSTRANT', 'ADMIN'] }
      },
      {
        path: 'consultar-manifestacao',
        loadComponent: () =>
          import('./pages/my-reports/report-list').then(m => m.MyReports),
        title: 'Égide - Consultar Manifestação',
        canActivate: [roleGuard],
        data: { roles: ['REMONSTRANT', 'ADMIN'] }
      },
      {
        path: 'manifestacoes',
        loadComponent: () => import('./pages/report-list/report-list').then(m => m.ReportList),
        title: 'Égide - Manifestações',
        canActivate: [roleGuard],
        data: { roles: ['LISTENER', 'GENERAL_LISTENER', 'MANAGER', 'ADMIN'] }
      },
      {
        path: 'manifestacoes/:id',
        loadComponent: () =>
          import('./pages/parecer-preliminar/parecer-preliminar').then(m => m.ParecerPreliminar),
        title: 'Égide - Detalhe da Manifestação',
        canActivate: [roleGuard],
        data: { roles: ['LISTENER', 'GENERAL_LISTENER', 'MANAGER', 'ADMIN'] }
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./pages/user-management/user-management').then(m => m.UserManagement),
        title: 'Égide - Usuários',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'notificacoes',
        loadComponent: () =>
          import('./pages/notifications/notifications').then(m => m.Notifications),
        title: 'Égide - Notificações'
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
