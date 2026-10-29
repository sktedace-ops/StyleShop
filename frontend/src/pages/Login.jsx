import {useEffect,useState} from "react";
import {useNavigate} from "react-router-dom";
import {request,setAccessToken} from "../api";
import PageActions from "../components/PageActions";

export default function Login({onLogin}){
  const [captcha,setCaptcha]=useState("");
  const [form,setForm]=useState({username:"",password:"",captcha:""});
  const [msg,setMsg]=useState("");
  const [loadingCaptcha,setLoadingCaptcha]=useState(false);
  const nav=useNavigate();

  async function refresh(){
    try{
      setLoadingCaptcha(true);
      setMsg("");
      const data=await request("/api/auth/captcha");
      setCaptcha(data?.captcha||"");
      if(!data?.captcha) setMsg("驗證碼取得失敗");
    }catch(e){
      setCaptcha("");
      setMsg(e.message);
    }finally{
      setLoadingCaptcha(false);
    }
  }

  useEffect(()=>{refresh()},[]);

  async function submit(e){
    e.preventDefault();
    if(!captcha){
      setMsg("目前無法取得驗證碼，請先確認 Spring Boot 後端已啟動");
      return;
    }
    if(form.captcha.length!==6){
      setMsg("請輸入 6 位數驗證碼");
      return;
    }
    try{
      setMsg("");
      const data=await request("/api/auth/login",{
        method:"POST",
        body:JSON.stringify(form)
      });
      setAccessToken(data.accessToken);
      await onLogin();
      nav("/");
    }catch(e){
      setMsg(e.message);
      setForm(prev=>({...prev,captcha:""}));
      await refresh();
    }
  }

  return <main className="narrow">
    <PageActions/>
    <div className="panel">
      <h1>會員登入</h1>
      {msg&&<p className="error">{msg}</p>}
      <form onSubmit={submit}>
        <label>帳號
          <input required value={form.username}
            onChange={e=>setForm({...form,username:e.target.value})}/>
        </label>
        <label>密碼
          <input required type="password" value={form.password}
            onChange={e=>setForm({...form,password:e.target.value})}/>
        </label>
        <label>6 位數驗證碼
          <div className="captcha">
            {loadingCaptcha?"載入中...":(captcha||"後端未連線")}
          </div>
          <input required inputMode="numeric" maxLength="6"
            value={form.captcha}
            onChange={e=>setForm({...form,captcha:e.target.value.replace(/\D/g,"")})}/>
        </label>
        <button className="primary" disabled={!captcha}>登入</button>
        <button type="button" onClick={refresh}>重新產生驗證碼</button>
      </form>
    </div>
  </main>;
}
