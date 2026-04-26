import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminComponent {}
