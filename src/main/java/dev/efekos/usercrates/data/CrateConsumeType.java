/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.usercrates.data;

import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.CustomArgument;

@CustomArgument("usercrates:crate_consume_type")
@Container
public enum CrateConsumeType {
    KEY(false,true), // The crate will get opened only using a key.
    PRICE(true,false), // The crate will get opened only paying a price
    BOTH_PRICE_KEY(true,true), // The crate will get opened with both keys and prices.
    ONLY_ACCESSORS(false,false); // Only the accessors can open the crate with no price/key. Intended for fun.

    private final boolean requireEconomy;
    private final boolean keyable;

    CrateConsumeType(boolean requireEconomy, boolean keyable) {
        this.requireEconomy = requireEconomy;
        this.keyable = keyable;
    }

    public boolean doesRequireEconomy() {
        return requireEconomy;
    }

    public boolean isKeyable() {
        return keyable;
    }
}
