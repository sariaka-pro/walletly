import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet, ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map, startWith } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { TranslatePipe } from '@ngx-translate/core';

interface NavItem {
  labelKey: string;
  path: string;
  icon: string;
}

interface BreadcrumbItem {
  label: string;
  url?: string;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppShellComponent implements OnInit {
  private readonly baseNavItems: NavItem[] = [
    { labelKey: 'nav.dashboard', path: '/dashboard', icon: 'dashboard' },
    { labelKey: 'nav.transactions', path: '/transactions', icon: 'swap_horiz' },
    { labelKey: 'nav.budgets', path: '/budgets', icon: 'account_balance_wallet' },
  ];

  get navItems(): NavItem[] {
    if (this.authService.isAdmin()) {
      return [...this.baseNavItems, { labelKey: 'nav.admin', path: '/admin', icon: 'admin_panel_settings' }];
    }
    return this.baseNavItems;
  }

  breadcrumbs = signal<BreadcrumbItem[]>([]);

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        startWith(null),
        map(() => {
          let route = this.activatedRoute;
          while (route.firstChild) {
            route = route.firstChild;
          }
          return route.snapshot.data;
        })
      )
      .subscribe((data) => {
        this.breadcrumbs.set((data['breadcrumbs'] as BreadcrumbItem[]) ?? []);
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
