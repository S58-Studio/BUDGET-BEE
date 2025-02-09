package com.financeAndMoney.legacy.domain.data

import android.icu.util.Currency
import androidx.compose.runtime.Immutable
import com.financeAndMoney.legacy.utils.getDefaultFIATCurrency

@Immutable
data class MysaveCurrency(
    val code: String,
    val name: String,
    val isCrypto: Boolean
) {
    companion object {
        private const val CRYPTO_DECIMAL = 18
        private const val FIAT_DECIMAL = 2

        private val CRYPTO = setOf(
            MysaveCurrency(
                code = "BTC",
                name = "Bitcoin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "ETH",
                name = "Ethereum",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "USDT",
                name = "Tether USD",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "BNB",
                name = "Binance Coin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "ADA",
                name = "Cardano",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "XRP",
                name = "Ripple",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "DOGE",
                name = "Dogecoin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "USDC",
                name = "USD Coin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "DOT",
                name = "Polkadot",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "UNI",
                name = "Uniswap",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "BUSD",
                name = "Binance USD",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "BCH",
                name = "Bitcoin Cash",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "SOL",
                name = "Solana",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "LTC",
                name = "Litecoin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "LINK",
                name = "ChainLink Token",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "SHIB",
                name = "Shiba Inu coin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "LUNA",
                name = "Terra",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "AVAX",
                name = "Avalanche",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "MATIC",
                name = "Polygon",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "CRO",
                name = "Cronos",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "WBTC",
                name = "Wrapped Bitcoin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "ALGO",
                name = "Algorand",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "XLM",
                name = "Stellar",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "MANA",
                name = "Decentraland",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "AXS",
                name = "Axie Infinity",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "DAI",
                name = "Dai",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "ICP",
                name = "Internet Computer",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "ATOM",
                name = "Cosmos",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "FIL",
                name = "Filecoin",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "ETC",
                name = "Ethereum Classic",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "DASH",
                name = "Dash",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "TRX",
                name = "Tron",
                isCrypto = true
            ),
            MysaveCurrency(
                code = "TON",
                name = "Tonchain",
                isCrypto = true
            ),
        )

        fun getAvailable(): List<MysaveCurrency> {
            return Currency.getAvailableCurrencies()
                .map {
                    MysaveCurrency(
                        code = it.currencyCode,
                        name = it.displayName,
                        isCrypto = false
                    )
                }
                .plus(CRYPTO)
        }

        fun fromCode(code: String): MysaveCurrency? {
            if (code.isBlank()) return null

            val crypto = CRYPTO.find { it.code == code }
            if (crypto != null) {
                return crypto
            }

            return try {
                val fiat = Currency.getInstance(code)
                MysaveCurrency(
                    fiatCurrency = fiat
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getDefault(): MysaveCurrency = MysaveCurrency(
            fiatCurrency = getDefaultFIATCurrency()
        )

        fun getDecimalPlaces(assetCode: String): Int =
            if (fromCode(assetCode) in CRYPTO) CRYPTO_DECIMAL else FIAT_DECIMAL
    }

    constructor(fiatCurrency: Currency) : this(
        code = fiatCurrency.currencyCode,
        name = fiatCurrency.displayName,
        isCrypto = false
    )
}
