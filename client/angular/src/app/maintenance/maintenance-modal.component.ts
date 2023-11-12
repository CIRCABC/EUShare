/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaintenanceService } from './maintenance.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-maintenance-modal',
  templateUrl: './maintenance-modal.component.html',
  styleUrl: './maintenance-modal.component.scss',
  standalone: true,
  imports: [CommonModule, MatIconModule],
})
export class MaintenanceModalComponent {
  modalOpen: boolean = true;

  constructor(private maintenanceService: MaintenanceService) {
    this.maintenanceService.getModalActionObservable().subscribe((isOpen) => {
      this.modalOpen = isOpen;
    });
  }
}
