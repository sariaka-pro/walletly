export interface User {
    id: number;
    email: string;
    role: 'USER' | 'ADMIN';
  }
  
  export interface LoginDto {
    email: string;
    password: string;
  }
  
  export interface AuthResponse {
    token: string;
    id?: number;
    email?: string;
  }