/**
 * EasyShare
 * This is a API definition for the EasyShare service.
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
     * Abreviated user name used for login
     */
    loginUsername: string;
    /**
     * Full name of the user
     */
    givenName?: string;
    /**
     * Email adress
     */
    email: string;
    /**
     * True if the user is admin
     */
    isAdmin: boolean;
}
