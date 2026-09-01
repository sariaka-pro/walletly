/**
 * Points d'entrée de l'API.
 *
 * Les URL sont relatives au domaine qui sert Angular :
 * - `ng serve` les transmet au backend grâce à `proxy.conf.json` ;
 * - Nginx les transmet au conteneur backend en production.
 */
export const API_ENDPOINTS = {
  auth: '/auth',
  admin: '/api/admin',
  budgets: '/api/budgets',
  categories: '/api/categories',
  expenses: '/api/expenses'
} as const;
