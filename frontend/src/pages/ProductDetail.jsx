import {useEffect,useState} from "react";
import {useNavigate,useParams,Link} from "react-router-dom";
import {request,imageUrl} from "../api";
import PageActions from "../components/PageActions";

export default function ProductDetail(){
  const {id}=useParams();
  const nav=useNavigate();
  const [p,setP]=useState(null);
  const [msg,setMsg]=useState("");
  const [notice,setNotice]=useState("");
  const [busy,setBusy]=useState(false);

  useEffect(()=>{
    request(`/api/products/${id}`)
      .then(setP)
      .catch(e=>{
        if(e.status===401) nav("/login");
        else setMsg(e.message);
      });
  },[id]);

  // 只加入購物車，不會建立訂單。
  async function addToCart(){
    try{
      setBusy(true);
      setMsg("");
      setNotice("");
      await request(`/api/cart/add/${id}`,{method:"POST"});
      setNotice("已加入購物車，尚未建立訂單。您可以繼續選購，或前往購物車確認商品。");
    }catch(e){
      if(e.status===401) nav("/login");
      else setMsg(e.message||"加入購物車失敗");
    }finally{
      setBusy(false);
    }
  }

  // 直接建立這項商品的獨立訂單，不會修改或清空購物車。
  async function addToOrder(){
    if(!window.confirm(`確定要將「${p.name}」直接加入訂單嗎？`)) return;
    try{
      setBusy(true);
      setMsg("");
      setNotice("");
      const order=await request(`/api/orders/direct/${id}`,{method:"POST"});
      nav(`/orders/${order.id}`);
    }catch(e){
      if(e.status===401) nav("/login");
      else setMsg(e.message||"建立訂單失敗");
    }finally{
      setBusy(false);
    }
  }

  if(!p){
    return <main><PageActions/><p>{msg||"載入中..."}</p></main>;
  }

  return <main className="detail-page">
    <PageActions/>
    <div className="detail">
      <img src={imageUrl(p.imageUrl)} alt={p.name}/>
      <section>
        <span className="badge">{p.categoryName}</span>
        <h1>{p.name}</h1>
        <h2>NT$ {Number(p.price).toLocaleString()}</h2>
        <p>{p.description}</p>
        {p.productUrl&&<a href={p.productUrl} target="_blank" rel="noreferrer">商品連結</a>}
        {msg&&<p className="error">{msg}</p>}
        {notice&&<div className="success-msg product-cart-notice">
          <span>{notice}</span>
          <Link className="cart-link-button" to="/cart">前往購物車</Link>
        </div>}
        <div className="product-order-actions">
          <button className="primary" disabled={busy} onClick={addToCart}>
            {busy?"處理中...":"加入購物車"}
          </button>
          <button className="order-now-button" disabled={busy} onClick={addToOrder}>
            {busy?"處理中...":"加入訂單"}
          </button>
        </div>
        <small className="muted">加入購物車不會建立訂單；按「加入訂單」才會直接建立一筆訂單。</small>
        <Link to="/">返回商品列表</Link>
      </section>
    </div>
  </main>;
}
