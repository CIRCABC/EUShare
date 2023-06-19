/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { TrustService } from '../../openapi/api/trust.service';
import { TrustRequest } from '../../openapi/model/trustRequest';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TrustDialogComponent } from '../trust-dialog/trust-dialog/trust-dialog.component';

@Component({
  selector: 'app-trust',
  templateUrl: './trust.component.html',
  styleUrls: ['./trust.component.scss'],
  standalone: true,
  imports: [CommonModule, MatDialogModule],
})
export class TrustComponent implements OnInit {
  trustRequests: TrustRequest[] = [];

  constructor(private trustService: TrustService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.trustService
      .getTrustRequestList()
      .subscribe((data: TrustRequest[]) => {
        this.trustRequests = data;
        console.log(data);
      });
  }

  onApprove(id: string | undefined): void {
    if (!id) {
      return;
    }
    this.trustService
      .approveTrustRequest(id, true)
      .subscribe(() => this.ngOnInit());
  }
  
  onDeny(id: string | undefined): void {
    if (!id) {
      return;
    }
    
    const dialogRef = this.dialog.open(TrustDialogComponent, {
      data: {},
    });
  
    dialogRef.afterClosed().subscribe((result) => {
      console.log('Text area content:', result);
      if (result) {
        console.log('Text area content:', result);
  
        this.trustService
          .approveTrustRequest(id, false, result)
          .subscribe(() => this.ngOnInit());
      }
    });
  }
  
  onDelete(id: string | undefined): void {
    if (!id) {
      return;
    }
    this.trustService.deleteTrustRequest(id).subscribe(() => this.ngOnInit());
  }
  
}
