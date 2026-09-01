import { HttpClient } from '@angular/common/http';
import { beforeEach, describe, expect, it } from 'vitest';
import { AuthService } from './auth.service';

function createToken(expirationInSeconds?: number): string {
  const payload = expirationInSeconds === undefined ? {} : { exp: expirationInSeconds };
  const encodedPayload = btoa(JSON.stringify(payload))
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');

  return `header.${encodedPayload}.signature`;
}

describe('AuthService', () => {
  beforeEach(() => localStorage.clear());

  it('considère un JWT non expiré comme valide', () => {
    localStorage.setItem('token', createToken(Math.floor(Date.now() / 1000) + 60));

    const service = new AuthService({} as HttpClient);

    expect(service.isAuthenticated()).toBe(true);
  });

  it('invalide un JWT expiré et supprime toutes les données de session', () => {
    localStorage.setItem('token', createToken(Math.floor(Date.now() / 1000) - 60));
    localStorage.setItem('userId', '42');
    localStorage.setItem('email', 'user@walletly.test');
    localStorage.setItem('firstName', 'Saria');
    localStorage.setItem('lastName', 'Kas');

    const service = new AuthService({} as HttpClient);

    expect(service.isAuthenticated()).toBe(false);
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('userId')).toBeNull();
    expect(localStorage.getItem('email')).toBeNull();
    expect(localStorage.getItem('firstName')).toBeNull();
    expect(localStorage.getItem('lastName')).toBeNull();
  });

  it('refuse un JWT mal formé ou dépourvu de date exp', () => {
    const service = new AuthService({} as HttpClient);

    expect(service.isTokenExpired('token-invalide')).toBe(true);
    expect(service.isTokenExpired(createToken())).toBe(true);
  });
});
