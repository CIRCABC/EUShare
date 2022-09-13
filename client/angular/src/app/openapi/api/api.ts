/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
export * from './admin.service';
import { AdminService } from './admin.service';
export * from './file.service';
import { FileService } from './file.service';
export * from './session.service';
import { SessionService } from './session.service';
export * from './users.service';
import { UsersService } from './users.service';
export const APIS = [AdminService, FileService, SessionService, UsersService];
