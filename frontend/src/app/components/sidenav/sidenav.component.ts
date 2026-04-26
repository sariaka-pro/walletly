import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatExpansionModule } from '@angular/material/expansion';

interface NavItem {
  icon: string;
  label: string;
  badge?: string | number;
  children?: NavItem[];
}

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatTooltipModule,
    MatBadgeModule,
    MatDividerModule,
    MatSidenavModule,
    MatExpansionModule,
  ],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidenavComponent {
  constructor(private authService: AuthService, private router: Router) {}

  expanded = signal(false);
  drawerOpen = signal(false);
  settingsOpen = signal(false);
  hasNotification = true;
  selectedProject = 'A very very long project';

  readonly projects = ['A very very long project', 'Something else'];

  readonly navItems: NavItem[] = [
    { icon: 'search', label: 'Search', badge: 12 },
    { icon: 'group', label: 'Groups' },
    {
      icon: 'settings',
      label: 'Settings',
      children: [
        { icon: '', label: 'Account' },
        { icon: '', label: 'Notifications' },
        { icon: '', label: 'Privacy' },
      ],
    },
    { icon: 'favorite', label: 'Favorites' },
    { icon: 'more_horiz', label: 'More' },
  ];

  readonly drawerItems = {
    Components: ['Button', 'Input', 'Tooltip'],
    Essentials: ['Getting started', 'Showcase', 'Typography'],
  };

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleExpanded(): void {
    this.expanded.update((v) => !v);
    if (this.settingsOpen()) {
      this.settingsOpen.set(false);
    }
  }

  toggleSettings(): void {
    if (this.expanded()) {
      this.settingsOpen.update((v) => !v);
    }
  }

  selectProject(project: string): void {
    this.selectedProject = project;
  }
}
