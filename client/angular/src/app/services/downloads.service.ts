/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DownloadsService {
  public async download(
    fileId: string,
    fileName: string,
    inputPassword?: string,
  ): Promise<'OK' | 'WRONG_PASSWORD' | 'TOO_MANY_DOWNLOADS'> {
    const url = `${environment.backend_url}/file/${fileId}${
      inputPassword === undefined ? '' : `?password=${inputPassword}`
    }`;
    if (inputPassword !== undefined) {
      const result = await fetch(url, {
        method: 'HEAD',
      });

      if (result.status === 401) {
        return 'WRONG_PASSWORD';
      }
      if (result.status === 429) {
        return 'TOO_MANY_DOWNLOADS';
      }
    }
    if (fileId && fileName) {
      const link = document.createElement('a');

      link.style.display = 'none';
      link.download = fileName;
      link.href = url;

      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
    return 'OK';
  }
}
