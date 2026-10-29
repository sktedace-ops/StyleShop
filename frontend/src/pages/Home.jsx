import {useEffect,useMemo,useState} from "react";
import {Link} from "react-router-dom";
import {request,imageUrl} from "../api";

export default function Home(){
  const [products,setProducts]=useState([]);
  const [cats,setCats]=useState([]);
  const [cat,setCat]=useState("");
  const [page,setPage]=useState(1);
  const [loading,setLoading]=useState(true);
  const [error,setError]=useState("");

  async function loadCategories(){
    try{
      const data=await request("/api/categories");
      setCats(Array.isArray(data)?data:[]);
    }catch(e){
      setError(e.message);
    }
  }

  async function loadProducts(){
    try{
      setLoading(true);
      setError("");
      const data=await request(cat?`/api/products?categoryId=${cat}`:"/api/products");
      setProducts(Array.isArray(data)?data:[]);
      setPage(1);
    }catch(e){
      setProducts([]);
      setError(e.message);
    }finally{
      setLoading(false);
    }
  }

  useEffect(()=>{loadCategories()},[]);
  useEffect(()=>{loadProducts()},[cat]);

  const shown=useMemo(()=>products.slice((page-1)*9,page*9),[products,page]);
  const pages=Math.ceil(products.length/9);

  return <main>
    <section className="hero">
      <h1>精選服飾</h1>
      <p>簡約・舒適・展現自我風格</p>
      <small>SIMPLE ・ COMFORTABLE ・ BE YOURSELF</small>
    </section>

    {error&&
      <div className="api-error panel">
        <h3>後端資料目前無法載入</h3>
        <p>{error}</p>
        <p>請確認另一個 Terminal 正在執行 <code>mvn spring-boot:run</code>。</p>
        <button onClick={()=>{loadCategories();loadProducts()}}>重新連線</button>
      </div>
    }

    <div className="filters">
      <button className={!cat?"active":""} onClick={()=>setCat("")}>全部商品</button>
      {cats.map(c=><button className={String(c.id)===String(cat)?"active":""}
        key={c.id} onClick={()=>setCat(c.id)}>{c.name}</button>)}
    </div>

    <div className="heading">
      <h2>{cat?(cats.find(c=>String(c.id)===String(cat))?.name+" 商品"):"全部商品"}</h2>
      <span>每頁最多 9 項商品</span>
    </div>

    {loading&&<div className="panel"><p>商品載入中...</p></div>}

    {!loading&&!error&&products.length===0&&
      <div className="panel"><h3>目前沒有商品資料</h3><p>請確認 MySQL 的 products 資料表是否有資料。</p></div>}

    {!loading&&products.length>0&&
      <section className="grid">
        {shown.map(p=><article className="card" key={p.id}>
          <Link to={`/product/${p.id}`}><img src={imageUrl(p.imageUrl)} alt={p.name}/></Link>
          <div>
            <span className="badge">{p.categoryName||"未分類"}</span>
            <h3>{p.name}</h3>
            <strong>NT$ {Number(p.price||0).toLocaleString()}</strong>
            <Link className="primary" to={`/product/${p.id}`}>查看商品 / 加入購物車</Link>
          </div>
        </article>)}
      </section>
    }

    {pages>1&&<div className="pagination">
      {Array.from({length:pages},(_,i)=><button className={page===i+1?"active":""}
        onClick={()=>setPage(i+1)} key={i}>{i+1}</button>)}
    </div>}
  </main>;
}
