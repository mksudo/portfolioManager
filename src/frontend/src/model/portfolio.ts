export interface IPortfolio {
    // generate id on server end
    id: null
    from: string
    to: string
    investment: {[symbol: string]: number}
}


interface ITimedResult<T> {
    time: string
    data: T
}

interface IIncomeResults {
    allocatedIncomes: {[symbol: string]: ITimedResult<number>[]}
    income: ITimedResult<number>[]
}

interface IReturnResults {
    allocatedReturns: {[symbol: string]: ITimedResult<number>[]}
    returns: ITimedResult<number>[]
}

export interface IPortfolioAnalyzeResult {
    portfolioIncomes: IIncomeResults
    spyIncomes: IIncomeResults
    portfolioReturns: IReturnResults
    spyReturns: IReturnResults

    overallReturn: {[symbol: string]: number}
    standardDeviation: number
    variances: {[percent: number]: number}
}