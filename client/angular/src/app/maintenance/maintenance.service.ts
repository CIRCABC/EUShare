/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MaintenanceService {
  private modalActionSubject = new Subject<boolean>();
  private keySequence: string[] = [];
  private secretCode: string = environment.maintenance_code;

  constructor() {
    this.listenForKeySequence();
  }

  getModalActionObservable() {
    return this.modalActionSubject.asObservable();
  }

  private listenForKeySequence() {
    window.addEventListener('keydown', (event) => {
      const key = event.key.toUpperCase();
      this.keySequence.push(key);

      if (
        this.keySequence.slice(-this.secretCode.length).join('') ===
        this.secretCode
      ) {
        this.modalActionSubject.next(false);
        this.keySequence = [];
      }

      if (this.keySequence.length > 100) {
        this.keySequence.shift();
      }
    });
  }
}
