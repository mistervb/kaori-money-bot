package br.com.victorbarberino.kaori.service;

public class CalcService {

    private static final double MINIMUM_PRICE = 0.0001;
    private static final double MINIMUM_FEES = 0;
    private static final double MAX_SLIPPAGE = 1;  // Slippage máximo de 100%

    /**
     * Calcula a margem de lucro, levando em consideração as taxas.
     * @param buyPrice Preço de compra
     * @param sellPrice Preço de venda
     * @param fees Taxas de transação
     * @return Margem de lucro percentual ou Double.NaN se os dados forem inválidos
     */
    public static double calculateProfitMargin(double buyPrice, double sellPrice, double fees) {
        // Validar entradas
        if (!isValidPrice(buyPrice) || !isValidPrice(sellPrice) || !isValidFees(fees)) {
            return Double.NaN;  // Indica valor inválido
        }

        // Cálculo da margem de lucro
        double profit = (sellPrice - buyPrice) - (sellPrice * fees);  // Taxas geralmente são aplicadas na venda
        return (profit / buyPrice) * 100;
    }

    /**
     * Calcula a margem de lucro, levando em consideração taxas e slippage.
     * @param buyPrice Preço de compra
     * @param sellPrice Preço de venda
     * @param fees Taxas de transação
     * @param slippage Slippage
     * @return Margem de lucro percentual ou Double.NaN se os dados forem inválidos
     */
    public static double calculateProfitMarginWithSlippage(double buyPrice, double sellPrice, double fees, double slippage) {
        // Validar entradas
        if (!isValidPrice(buyPrice) || !isValidPrice(sellPrice) || !isValidFees(fees) || !isValidSlippage(slippage)) {
            return Double.NaN;  // Indica valor inválido
        }

        // Ajustar preço de venda com slippage
        double adjustedSellPrice = sellPrice - (sellPrice * slippage);

        // Cálculo da margem de lucro
        double profit = (adjustedSellPrice - buyPrice) - (adjustedSellPrice * fees);  // Taxas geralmente sobre venda
        return (profit / buyPrice) * 100;
    }

    /**
     * Valida o preço para garantir que ele é maior que o mínimo.
     * @param price Preço
     * @return true se o preço for válido
     */
    private static boolean isValidPrice(double price) {
        return price >= MINIMUM_PRICE;
    }

    /**
     * Valida as taxas para garantir que elas não são negativas.
     * @param fees Taxas
     * @return true se as taxas forem válidas
     */
    private static boolean isValidFees(double fees) {
        return fees >= MINIMUM_FEES;
    }

    /**
     * Valida o slippage para garantir que ele esteja entre 0 e 100% (0 e 1.0).
     * @param slippage Slippage
     * @return true se o slippage for válido
     */
    private static boolean isValidSlippage(double slippage) {
        return slippage >= 0 && slippage <= MAX_SLIPPAGE;
    }
}
