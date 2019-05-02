/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { faUpload, faFile, faUsers, faShare, faCloudDownloadAlt, faShareAlt } from '@fortawesome/free-solid-svg-icons';
import { LoginService } from '../service/login.service';
import { SessionService } from '../openapi';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  faUpload = faUpload;
  faUsers = faUsers;
  faShare = faShare;
  faCloudDownloadAlt = faCloudDownloadAlt;
  faShareAlt = faShareAlt;

  constructor(private service: SessionService) {}

  ngOnInit() {}

  logout() {
    this.service.logout();
  }

  get loggedIn(): boolean {
    return this.service.getStoredCredentials() !== null 
  }
}
