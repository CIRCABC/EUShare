export * from './file.service';
import { FileService } from './file.service';
export * from './session.service';
import { SessionService } from './session.service';
export * from './users.service';
import { UsersService } from './users.service';
export const APIS = [FileService, SessionService, UsersService];
