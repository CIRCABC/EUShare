/**
 * CIRCABC Share
 * This is a API definition for the CIRCABC Share service.
 *
 * The version of the OpenAPI document: 0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface UserInfoAllOf { 
    /**
     * User ID
     */
    id: string;
    /**
     * Abbreviated user name used for login
     */
    loginUsername: string;
    /**
     * Full name of the user
     */
    givenName?: string;
    /**
     * Email address
     */
    email: string;
    /**
     * role
     */
    role?: UserInfoAllOf.RoleEnum;
    /**
     * True if the user is admin
     */
    isAdmin: boolean;
    /**
     * status = REGULAR - Users who have full access to the site; FROZEN - Users who can log in and download, but cannot upload; PURGED - Users who can log in, download, but cannot upload, and their files get deleted; BANNED - Users who cannot log in at all.
     */
    status?: UserInfoAllOf.StatusEnum;
}
export namespace UserInfoAllOf {
    export type RoleEnum = 'ADMIN' | 'INTERNAL' | 'EXTERNAL' | 'TRUSTED_EXTERNAL' | 'API_KEY';
    export const RoleEnum = {
        ADMIN: 'ADMIN' as RoleEnum,
        INTERNAL: 'INTERNAL' as RoleEnum,
        EXTERNAL: 'EXTERNAL' as RoleEnum,
        TRUSTEDEXTERNAL: 'TRUSTED_EXTERNAL' as RoleEnum,
        APIKEY: 'API_KEY' as RoleEnum
    };
    export type StatusEnum = 'REGULAR' | 'FROZEN' | 'PURGED' | 'BANNED' | 'UNKNOWN';
    export const StatusEnum = {
        REGULAR: 'REGULAR' as StatusEnum,
        FROZEN: 'FROZEN' as StatusEnum,
        PURGED: 'PURGED' as StatusEnum,
        BANNED: 'BANNED' as StatusEnum,
        UNKNOWN: 'UNKNOWN' as StatusEnum
    };
}


