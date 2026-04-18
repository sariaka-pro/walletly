import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatisticsChart } from './statistics-chart.component';

describe('StatisticsChart', () => {
  let component: StatisticsChart;
  let fixture: ComponentFixture<StatisticsChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatisticsChart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatisticsChart);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
