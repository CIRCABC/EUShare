import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaintenanceService } from './maintenance.service';
import { MatIconModule } from '@angular/material/icon';


@Component({
  selector: 'app-maintenance-modal',
  templateUrl:  './maintenance-modal.component.html',
  styleUrl: './maintenance-modal.component.scss',
  standalone: true,
  imports: [CommonModule,MatIconModule],
})
export class MaintenanceModalComponent {
  modalOpen: boolean = true;

  constructor(private maintenanceService: MaintenanceService) {
    this.maintenanceService.getModalActionObservable().subscribe((isOpen) => {
      this.modalOpen = isOpen;
    });
  }
}