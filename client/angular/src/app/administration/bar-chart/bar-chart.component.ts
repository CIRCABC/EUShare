import { Component, Input, OnInit,ViewChild } from '@angular/core';
import { ChartConfiguration, ChartType,  } from 'chart.js';

@Component({
  selector: 'app-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.scss']
})
export class BarChartComponent implements OnInit {
  @Input() chartData: { labels: string[]; datasets: { data: number[]; label: string; }[] ; } =  {
    labels: [  ],
    datasets: [
      { data: [ ], label: '' }
    ]
  };

  barChartData: { labels: string[]; datasets: { data: number[]; label: string; }[] ; };
  constructor() { 
    this.barChartData = {
      labels: [  ],
      datasets: [
        { data: [ ], label: '' }
      ]
    };
  }

  ngOnInit(): void {
  }

  ngOnChanges(){
    if(this.chartData){
        this.barChartData = this.chartData;
       
    }
  }
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,

    scales: {

    },
    plugins: {
      legend: {
        display: true,
      }
    }
  };
  public barChartType: ChartType = 'bar';
  public barChartColors: any[] = [
    { backgroundColor: 'blue' }
   ];



}
