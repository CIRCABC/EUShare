/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ChartData, ChartOptions, ChartType } from 'chart.js';

@Component({
  selector: 'app-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.scss'],
})
export class BarChartComponent implements OnChanges {
  @Input() chartData: ChartData = {
    labels: ['', '', '', '', '', '', '', '', '', '', '', ''],
    datasets: [{ data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], label: '' }],
  };

  barChartData: {
    labels: string[];
    datasets: { data: number[]; label: string }[];
  };
  constructor() {
    this.barChartData = {
      labels: ['', '', '', '', '', '', '', '', '', '', '', ''],
      datasets: [{ data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], label: '' }],
    };
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['chartData']) {
      this.barChartData = changes['chartData'].currentValue;
    }
  }
  public barChartOptions: ChartOptions = {
    backgroundColor: '#58c37e',
    responsive: true,

    scales: {},
    plugins: {
      legend: {
        display: true,
      },
    },
  };
  public barChartType: ChartType = 'bar';
  public barChartColors = [{ backgroundColor: 'blue' }];
}
