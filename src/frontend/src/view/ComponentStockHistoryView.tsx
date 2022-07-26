import React from "react";
import {IComponentStockHistoryProp} from "./components/ComponentStockHistory";
import {ComponentStockHistoryPresenter} from "./components/ComponentStockHistoryPresenter";

type IComponentStockHistoryViewState = {
    currSymbol: string
    currFrom: Date
    currTo: Date
    histories: IComponentStockHistoryProp[]
}

export class ComponentStockHistoryView extends React.Component<{}, IComponentStockHistoryViewState> {

    constructor(props: {}) {
        super(props);

        this.state = {
            currSymbol: "",
            currFrom: new Date(),
            currTo: new Date(),
            histories: []
        }

        this.handleSymbolChange = this.handleSymbolChange.bind(this)
        this.handleFromDateChange = this.handleFromDateChange.bind(this)
        this.handleToDateChange = this.handleToDateChange.bind(this)
        this.handleGetComponentStockHistoriesButtonClicked =
            this.handleGetComponentStockHistoriesButtonClicked.bind(this)
    }

    getComponentStockHistories(symbol: string, from: Date, to: Date) {
        fetch(
            `https://localhost:8080/histories?symbol=${symbol}&from=${from.toUTCString()}&to=${to.toUTCString()}`
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
            this.state.currFrom,
            this.state.currTo
        )
    }

    handleSymbolChange(event: React.FormEvent<HTMLInputElement>) {
        this.setState({
            currSymbol: event.currentTarget.value
        })
    }

    handleFromDateChange(event: React.FormEvent<HTMLInputElement>) {
        const nextFromDate = new Date(event.currentTarget.value);
        this.setState({
            currFrom: nextFromDate
        })
    }

    handleToDateChange(event: React.FormEvent<HTMLInputElement>) {
        const nextToDate = new Date();
        nextToDate.setTime(Date.parse(event.currentTarget.value))
        this.setState({
            currTo: nextToDate
        })
    }

    render() {
        return (
            <div>
                <div>
                    <span>Please input symbol: </span>
                    <input type={"text"} value={this.state.currSymbol} onChange={this.handleSymbolChange}/>
                </div>
                <div>
                    <span>Please input from date: </span>
                    <input type={"datetime-local"} value={this.state.currFrom.toTimeString()} onChange={this.handleFromDateChange}/>
                </div>
                <div>
                    <span>Please input to date: </span>
                    <input type={"datetime-local"} value={this.state.currTo.toTimeString()} onChange={this.handleToDateChange}/>
                </div>
                <div>
                    <button onClick={this.handleGetComponentStockHistoriesButtonClicked}>Get {this.state.currSymbol} component stock histories</button>
                </div>
                <ComponentStockHistoryPresenter histories={this.state.histories}/>
            </div>
        )
    }
}