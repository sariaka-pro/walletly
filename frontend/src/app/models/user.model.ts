export interface User {
    id: number;
    email: string;
    role: 'USER' | 'ADMIN';
  }
  
  export interface LoginDto {
    email: string;
    password: string;
  }

    export interface RegisterDto {
      email: string;
      password: string;
      firstName: string;
      lastName: string;
    }
  
  export interface AuthResponse {
    token: string;
    id?: number;
    email?: string;
    firstName?: string;
    lastName?: string;
  }