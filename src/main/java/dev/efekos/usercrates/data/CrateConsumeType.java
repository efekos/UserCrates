package dev.efekos.usercrates.data;

public enum CrateConsumeType {
    KEY, // The crate will get opened only using a key.
    PRICE, // The crate will get opened only paying a price
    BOTH_PRICE_KEY, // The crate will get opened with both keys and prices.
    ONLY_ACCESSORS // Only the accessors can open the crate with no price/key. Intended for fun.
}
