import React, { Component } from 'react'

class EnterToken extends Component {
    constructor(props) {
        super(props)
    
        this.state = {
             token: ''
        }

        this.submit = this.submit.bind(this);
    }

    submit = e => {
        e.preventDefault();
        if(this.state.token==this.props.location.state.token){
            this.props.history.push({
                pathname : '/reset-password',
                state:{
                    email:this.props.location.state.email
                }
            })
        }else{
            alert("Incorrect token");
            this.props.history.push('/userLogin');
        }

    }

    onChange = e => this.setState({ token: e.target.value });
    
    render() {
        return (
            <>
                <h2 className="text-center mt-3 ">Enter Token</h2>
                <form className="container bg-dark pt-2 mt-3" style={{width: "30vw"}}>
                    <div className="form-group">
                        <input type="text" className="form-control text-center mt-3" placeholder="Enter Token" name="token" value={this.state.token} onChange={this.onChange} required />
                    </div>
                    <button className="btn btn-primary my-3 offset-5" onClick={this.submit}>SUBMIT</button>
                </form>
            </>
        )
    }
}

export default EnterToken
