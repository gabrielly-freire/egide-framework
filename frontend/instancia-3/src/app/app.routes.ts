import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.LoginPage),
  },
  {
    path: 'portal',
    loadComponent: () => import('./pages/portal/portal').then(m => m.PortalPage),
  },
  {
    path: '',
    loadComponent: () => import('./layout/layout').then(m => m.Layout),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.DashboardPage),
      },
      {
        path: 'manifestacoes',
        loadComponent: () =>
          import('./pages/manifestations/list/manifestation-list').then(
            m => m.ManifestationListPage,
          ),
      },
      {
        path: 'manifestacoes/:id',
        loadComponent: () =>
          import('./pages/manifestations/detail/manifestation-detail').then(
            m => m.ManifestationDetailPage,
          ),
      },
      {
        path: 'analistas',
        loadComponent: () => import('./pages/analysts/analysts').then(m => m.AnalystsPage),
        canActivate: [roleGuard(['ADMIN'])],
      },
      {
        path: 'relatorios',
        loadComponent: () => import('./pages/reports/reports').then(m => m.ReportsPage),
      },
    ],
  },
  { path: '**', redirectTo: '/dashboard' },
];
