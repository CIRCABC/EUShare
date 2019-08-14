/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { FolderInfo } from '../interfaces/folder-info';
import { InterestGroup } from '../interfaces/interest-group';
import { Membership } from '../interfaces/membership';

@Injectable({
  providedIn: 'root'
})
export class CircabcService {
  /**
   * (get interest groups) /users/{userId}/memberships
   */
  getUserMembership(userId: string): Promise<Membership> {
    const membership: Membership = {
      interestGroups: [
        {
          name: 'MyInterestGroup1',
          id: 'interestgroupiddonotuselikethis1',
          libraryId: 'libraryiddonotuselikethis1'
        },
        {
          name: 'MyInterestGroup2',
          id: 'interestgroupiddonotuselikethis2',
          libraryId: 'libraryiddonotuselikethis2'
        }
      ]
    };

    return Promise.resolve(membership);
  }

  getInterestGroupFolders(interestGroup: InterestGroup): Promise<FolderInfo[]> {
    const folderInfo: FolderInfo[] = [
      {
        name: 'Folder1',
        subFolders: [
          {
            name: 'Folder1.1',
            subFolders: [],
            subFiles: []
          },
          {
            name: 'Folder1.2',
            subFolders: [],
            subFiles: []
          }
        ],
        subFiles: []
      }
    ];
    return Promise.resolve(folderInfo);
  }

  constructor() {}
}
