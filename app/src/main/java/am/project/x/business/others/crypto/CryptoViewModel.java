/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.project.x.business.others.crypto;

/**
 * ViewModel
 */
interface CryptoViewModel {
    /**
     * 获取信息
     *
     * @param input 输入
     */
    void getMessage(String input);

    /**
     * DES加密
     *
     * @param input 输入
     */
    void getDES(String input);

    /**
     * AES加密
     *
     * @param input 输入
     */
    void getAES(String input);

    /**
     * RSA加密
     *
     * @param input 输入
     */
    void getRSA(String input);
}
