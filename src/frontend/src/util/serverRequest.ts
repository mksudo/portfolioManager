import {IComponentStock} from "../model/componentStock";
import {IPortfolio, IPortfolioAnalyzeResult} from "../model/portfolio";

const BASE_URL = "http://localhost:8080"


export async function requestAllComponentStocks() {
    const response = await fetch(`${BASE_URL}/stock/find/all`, {
        method: "GET", mode: "cors"
    })
    const responseBody: IComponentStock[] = await response.json()
    return responseBody
}

export async function requestComponentStock(symbol: string) {
    const response = await fetch(`${BASE_URL}/stock/find?symbol=${symbol}`, {
        method: "GET", mode: "cors"
    })
    const responseBody: IComponentStock = await response.json()
    return responseBody
}

export async function requestAnalyzedPortfolio(portfolio: IPortfolio) {
    const response = await fetch(`${BASE_URL}/portfolio/analyze`, {
        method: "POST",
        mode: "cors",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(portfolio)
    })
    const responseBody: IPortfolioAnalyzeResult = await response.json()
    return responseBody
}