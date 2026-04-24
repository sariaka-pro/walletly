import { Component, Input, input } from '@angular/core';

@Component({
  selector: 'app-kpi-card',
  imports: [],
  standalone: true,
  templateUrl: './kpi-card.component.html',
  styleUrl: './kpi-card.component.css',
})
export class KpiCardComponent {


  @Input() title!: string;
  @Input() value!:  number;
  @Input() unit!: string;
  @Input()  icon!: string;
  
}
