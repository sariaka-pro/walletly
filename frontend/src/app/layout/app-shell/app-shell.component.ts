import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet, ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map, startWith } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

interface NavItem {
  label: string;
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
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppShellComponent implements OnInit {
  readonly navItems: NavItem[] = [
    { label: 'Dashboard', path: '/dashboard', icon: 'dashboard' },
    { label: 'Transactions', path: '/transactions', icon: 'swap_horiz' },
    { label: 'Budgets', path: '/budgets', icon: 'account_balance_wallet' },
    { label: 'Savings Goals', path: '/savings-goals', icon: 'savings' },
    { label: 'Analytics', path: '/analytics', icon: 'bar_chart' },
  ];

  breadcrumbs = signal<BreadcrumbItem[]>([]);
  tabs = signal<string[]>([]);
  activeTab = signal<string>('');

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
        const tabList = (data['tabs'] as string[]) ?? [];
        this.tabs.set(tabList);
        if (tabList.length > 0 && !tabList.includes(this.activeTab())) {
          this.activeTab.set(tabList[0]);
        }
      });
  }

  selectTab(tab: string): void {
    this.activeTab.set(tab);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
