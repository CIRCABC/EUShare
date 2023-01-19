/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Injectable } from '@angular/core';
import { KEYUTIL, KJUR, RSAKey, stob64u } from 'jsrsasign';

/**
 * We want this class to be a singleton.
 *
 * There are two ways to make a service a singleton in Angular:
 *  - Declare that the service should be provided in the application root.
 *  - Include the service in the AppModule or in a module that is only imported by the AppModule.
 *
 *  We have defined that this service should be "provided" in the AppModule, thus making it a Singleton
 */
@Injectable()
export class KeyStoreService {
  private readonly _PRVKEY = 'prvKey';
  private readonly _PUBKEY = 'pubKey';

  constructor() {
    this.load();
  }

  private load = () => {
    const available =
      localStorage.getItem(this._PRVKEY) !== null &&
      localStorage.getItem(this._PUBKEY) !== null;

    if (!available) {
      const keystore = KEYUTIL.generateKeypair('EC', 'secp256r1');
      localStorage.setItem(
        this._PRVKEY,
        JSON.stringify(KEYUTIL.getJWKFromKey(keystore['prvKeyObj']))
      );
      localStorage.setItem(
        this._PUBKEY,
        JSON.stringify(KEYUTIL.getJWKFromKey(keystore['pubKeyObj']))
      );
    }
  };

  /**
   * Returns the public key instance from the generate key pair
   */
  private publicKey = () => {
    const pubKey = localStorage.getItem(this._PUBKEY);
    if (pubKey !== null) {
      return KEYUTIL.getKey(JSON.parse(pubKey));
    }

    throw Error(
      'Keystore service not correctly initialized, cannot obtain public key'
    );
  };

  /**
   * Returns the private key instance from the generate key pair
   */
  private privateKey() {
    const privKey = localStorage.getItem(this._PRVKEY);
    if (privKey !== null) {
      return KEYUTIL.getKey(JSON.parse(privKey));
    }

    throw Error(
      'Keystore service not correctly initialized, cannot obtain private key'
    );
  }

  /**
   * Returns the public key instance in JWK format
   */
  publicKeyAsJWK = () =>
    KEYUTIL.getJWKFromKey(this.publicKey() as RSAKey | KJUR.crypto.ECDSA);

  /**
   * Returns the JWK public key instances in a Ba64URL encoded format
   */
  publicJWKBase64UrlEncoded = () =>
    stob64u(JSON.stringify(this.publicKeyAsJWK()));

  /**
   * Returns a random hex-string of <code>n</code> bytes
   *
   * @param n the number of bytes to allocate
   */
  randomHex = (n: number) => KJUR.crypto.Util.getRandomHexOfNbytes(n);

  /**
   * Create a JWS from a provided JWT payload using the stored private key
   *
   * @param jwtPayload
   */
  signJWT = (jwtPayload: object) => {
    const privateKey: any = this.privateKey();
    const jwtHeader = { alg: 'ES256', cty: 'JWT' };
    return KJUR.jws.JWS.sign('ES256', jwtHeader, jwtPayload, privateKey);
  };

  /**
   * Verify a JWS payload with the stored public key
   *
   * @param payload
   */
  verifyJws = (payload: string) => {
    const publicKey: any = this.publicKey();
    return KJUR.jws.JWS.verify(payload, publicKey);
  };

  /**
   * Remove all entries from the temp storage
   */
  clear = () => {
    localStorage.removeItem(this._PRVKEY);
    localStorage.removeItem(this._PUBKEY);
  };

  /**
   * Remove all the entries from the temp storage and re-init the keystore
   */
  prepareKeyStore = () => {
    this.clear();
    this.load();
  };
}
