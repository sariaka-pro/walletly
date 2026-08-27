import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { filter } from 'rxjs/operators';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';


@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TranslatePipe,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isRegisterMode = false;
  authError: string | null = null;
  authInfo: string | null = null;


  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService,
  ) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      firstName: [''],
      lastName: [''],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });

    this.syncModeFromUrl();
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => this.syncModeFromUrl());
  }


  private syncModeFromUrl(): void {
    this.isRegisterMode = this.router.url.startsWith('/register');

    const firstNameControl = this.loginForm.get('firstName');
    const lastNameControl = this.loginForm.get('lastName');

    if (this.isRegisterMode) {
      firstNameControl?.setValidators([Validators.required]);
      lastNameControl?.setValidators([Validators.required]);
    } else {
      firstNameControl?.clearValidators(); //vide les champs 
      lastNameControl?.clearValidators();
      firstNameControl?.setValue('');
      lastNameControl?.setValue('');
    }

    firstNameControl?.updateValueAndValidity({ emitEvent: false }); // recalcule la validité sans déclencher d'events inutiles 
    lastNameControl?.updateValueAndValidity({ emitEvent: false });
  }


  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched(); //affiche les erreurs dans l'UI
      this.authError = this.isRegisterMode
        ? this.translate.instant('auth.register.errors.required')
        : this.translate.instant('auth.login.errors.required');
      this.authInfo = null;
      return;
    }

    if (this.isRegisterMode) {
      this.register();
      return;
    }

    this.login();
  }


  private login(): void {
    const { email, password } = this.loginForm.value;
    this.authError = null;
    this.authInfo = null;

    this.authService.login({ email, password }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.authError = this.translate.instant('auth.login.errors.invalidCredentials');
      }
    });
  }


  private register(): void {
    const { firstName, lastName, email, password } = this.loginForm.value;
    this.authError = null;
    this.authInfo = null;

    if (!String(firstName ?? '').trim() || !String(lastName ?? '').trim()) {
      this.authError = this.translate.instant('auth.register.errors.nameRequired');
      return;
    }

    this.authService.register({
      firstName: String(firstName).trim(),
      lastName: String(lastName).trim(),
      email,
      password,
    }).subscribe({
      next: () => {
        this.authInfo = this.translate.instant('auth.register.info.createdAndLogin');
        this.authService.login({ email, password }).subscribe({
          next: () => this.router.navigate(['/dashboard']),
          error: () => {
            this.authInfo = this.translate.instant('auth.register.info.createdLoginNow');
          }
        });
      },
      error: (err: any) => {
        const backendMessage = String(err?.error?.message ?? '').toLowerCase();
        if (backendMessage.includes('already exists')) {
          this.authError = this.translate.instant('auth.register.errors.emailExists');
          return;
        }
        this.authError = this.translate.instant('auth.register.errors.registerFailed');
      }
    });
  }


  goToRegister(): void {
    this.authError = null;
    this.authInfo = null;
    this.router.navigate(['/register']);
  }


  goToLogin(): void {
    this.authError = null;
    this.authInfo = null;
    this.router.navigate(['/login']);
  }
  
}
