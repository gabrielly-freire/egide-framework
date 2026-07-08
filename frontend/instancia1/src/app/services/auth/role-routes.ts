export function landingRouteFor(role: string | null | undefined): string {
  switch ((role || '').toUpperCase()) {
    case 'REMONSTRANT':
      return '/consultar-manifestacao';
    case 'LISTENER':
    case 'GENERAL_LISTENER':
    case 'MANAGER':
    case 'ADMIN':
      return '/dashboard';
    default:
      return '/login';
  }
}
