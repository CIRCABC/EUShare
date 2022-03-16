/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/


import { InjectionToken } from '@angular/core';

export const ALF_BASE_PATH = new InjectionToken<string>('alfBasePath');
export const CBC_BASE_PATH = new InjectionToken<string>('cbcBasePath');
export const SERVER_URL = new InjectionToken<string>('serverURL');
export const APP_VERSION = new InjectionToken<string>('appVersion');
export const APP_ALF_VERSION = new InjectionToken<string>('appAlfVersion');
export const NODE_NAME = new InjectionToken<string>('nodeName');
export const BUILD_DATE = new InjectionToken<string>('buildDate');
