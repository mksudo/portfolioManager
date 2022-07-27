import React from "react";
import {IComponentStockHistoryProp} from "./components/ComponentStockHistory";
import {ComponentStockHistoryPresenter} from "./components/ComponentStockHistoryPresenter";

type IComponentStockHistoryViewState = {
    currSymbol: string
    currFrom: string
    currTo: string
    histories: IComponentStockHistoryProp[]
}

function parseDateToString(date: Date) {
    return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, -1)
}

function parseStringToDate(localDateString: string) {
    const fakeTime = new Date(localDateString)
    return new Date(fakeTime.getTime() - fakeTime.getTimezoneOffset() * 60000)
}

export class ComponentStockHistoryView extends React.Component<{}, IComponentStockHistoryViewState> {

    constructor(props: {}) {
        super(props);

        const initialTime = parseDateToString(new Date())

        this.state = {
            currSymbol: "",
            currFrom: initialTime,
            currTo: initialTime,
            histories: []
        }

        this.handleSymbolChange = this.handleSymbolChange.bind(this)
        this.handleFromDateChange = this.handleFromDateChange.bind(this)
        this.handleToDateChange = this.handleToDateChange.bind(this)
        this.handleGetComponentStockHistoriesButtonClicked =
            this.handleGetComponentStockHistoriesButtonClicked.bind(this)
    }

    getComponentStockHistories(symbol: string, from: string, to: string) {
        fetch(
            `http://localhost:8080/history/find?symbol=${symbol}&from=${from}&to=${to}`,
            {
                method: "GET",
                mode: "cors"
            }
        ).then(
            response => response.json()
        ).then(
            (jsonBody: IComponentStockHistoryProp[]) => {
                this.setState({
                    histories: jsonBody
                })
            }
        )
    }

    handleGetComponentStockHistoriesButtonClicked(event: React.FormEvent<HTMLButtonElement>) {
        this.getComponentStockHistories(
            this.state.currSymbol,
            this.state.currFrom + "Z",
            this.state.currTo + "Z"
        )
    }

    handleSymbolChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState({
            currSymbol: event.currentTarget.value
        })
    }

    handleFromDateChange(event: React.FormEvent<HTMLInputElement>) {
        const nextFromDate = parseStringToDate(event.currentTarget.value).toISOString().slice(0, -1)
        this.setState({
            currFrom: nextFromDate
        })
    }

    handleToDateChange(event: React.FormEvent<HTMLInputElement>) {
        const nextToDate = parseStringToDate(event.currentTarget.value).toISOString().slice(0, -1)
        this.setState({
            currTo: nextToDate
        })
    }

    render() {
        return (
            <div>
                <div className={"component-history-input-wrapper"}>
                    <span>Please input symbol: </span>
                    <input type={"text"} value={this.state.currSymbol} onChange={this.handleSymbolChange}/>
                </div>
                <div className={"component-history-input-wrapper"}>
                    <span>Please input from date: </span>
                    <input type={"datetime-local"} value={this.state.currFrom} onChange={this.handleFromDateChange}/>
                </div>
                <div className={"component-history-input-wrapper"}>
                    <span>Please input to date: </span>
                    <input type={"datetime-local"} value={this.state.currTo} onChange={this.handleToDateChange}/>
                </div>
                <div className={"component-history-button-wrapper"}>
                    <button onClick={this.handleGetComponentStockHistoriesButtonClicked}>Get {this.state.currSymbol} component stock histories</button>
                </div>
                <ComponentStockHistoryPresenter histories={this.state.histories}/>
            </div>
        )
    }
}