/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { saveAs } from 'file-saver';
import { environment } from '../../environments/environment';
import { FileInfo } from '../interfaces/file-info';
import { PutUserShares } from '../interfaces/put-user-shares';
import { Recipient } from '../interfaces/recipient';
import { UserInfo } from '../interfaces/user-info';
import { LoginService } from './login.service';

@Injectable()
export class ApiService {
  private apiUrl = environment.backend_url;

  private get header() {
    return {
      'ES-Session': this.login.sessionId
    };
  }

  private get options() {
    return {
      headers: this.header
    };
  }

  constructor(private http: HttpClient, private login: LoginService) {}

  deleteFile(fileId: string): Promise<void> {
    return this.http
      .post<void>(
        `${this.apiUrl}/files/${fileId}/delete`,
        undefined,
        this.options
      )
      .toPromise();
  }

  async download(fileId: string, password?: string): Promise<void> {
    const postBody = password !== undefined ? btoa(password) : undefined;

    const response = await this.http
      .post(
        `${this.apiUrl}/files/${encodeURIComponent(fileId)}/download`,
        postBody,
        {
          headers: this.header,
          observe: 'response',
          responseType: 'blob'
        }
      )
      .toPromise();

    if (response.body == null) {
      return;
    }

    const disposition = response.headers.get('Content-Disposition');

    if (disposition == null) {
      return saveAs(response.body);
    }

    const regex = /filename[^;\n=]*=((['"])(.*?)\2|[^;\n]*)/g;
    const matches = regex.exec(disposition);

    if (matches == null) {
      return saveAs(response.body);
    }

    try {
      const filename = matches[3];
      return saveAs(response.body, filename);
    } catch {
      return saveAs(response.body);
    }
  }

  getFile(fileId: string): Promise<FileInfo> {
    return this.http
      .get<FileInfo>(
        `${this.apiUrl}/files/${encodeURIComponent(fileId)}`,
        this.options
      )
      .toPromise();
  }

  getFiles(): Promise<FileInfo[]> {
    return this.http
      .get<FileInfo[]>(`${this.apiUrl}/files`, this.options)
      .toPromise();
  }

  getMe(): Promise<UserInfo> {
    return this.http
      .get<UserInfo>(`${this.apiUrl}/me`, this.options)
      .toPromise();
  }

  getUser(userId: string): Promise<UserInfo> {
    return this.http
      .get<UserInfo>(
        `${this.apiUrl}/users/${decodeURIComponent(userId)}`,
        this.options
      )
      .toPromise();
  }

  // postFiles
  private requestNewFile(
    size: number,
    expirationDate?: Date,
    password?: string
  ): Promise<string> {
    const body = {
      expirationDate,
      filesize: size,
      password: password !== undefined ? btoa(password) : undefined
    };

    return this.http
      .post(`${this.apiUrl}/files`, body, {
        responseType: 'text',
        headers: this.header
      })
      .toPromise();
  }

  async postSharedWith(
    fileId: string,
    add: Recipient[],
    remove: string[]
  ): Promise<void> {
    const shares: PutUserShares = {
      add,
      remove
    };

    try {
      await this.http
        .post(`${this.apiUrl}/files/${fileId}/users`, shares, this.options)
        .toPromise();
    } catch {
      return Promise.reject('Putting failed');
    }
  }

  async uploadFile(
    file: File,
    shareWith: Recipient[],
    expirationDate?: Date,
    password?: string
  ): Promise<FileInfo> {
    let fileId: string;

    try {
      fileId = await this.requestNewFile(file.size, expirationDate, password);
    } catch (e) {
      return Promise.reject('Could not request new file');
    }

    let result: FileInfo;

    try {
      const body = new FormData();
      body.append('data', file);

      result = await this.http
        .post<FileInfo>(`${this.apiUrl}/files/${fileId}`, body, this.options)
        .toPromise();
    } catch {
      return Promise.reject('Could not upload file');
    }

    if (shareWith) {
      try {
        await this.postSharedWith(result.id, shareWith, []);
      } catch {
        console.error('Could not set shares');
      }
    }

    return result;
  }
}
