import {useEffect,useState} from "react";
import {Link} from "react-router-dom";
import {request,imageUrl} from "../api";

export default function Home(){
  const [products,setProducts]=useState([]);
  const [cats,setCats]=useState([]);
  const [cat,setCat]=useState("");
  const [page,setPage]=useState(0);
  const [filters,setFilters]=useState({keyword:"",minPrice:"",maxPrice:"",sortBy:"id",direction:"asc"});
  const [applied,setApplied]=useState(filters);
  const [meta,setMeta]=useState({totalElements:0,totalPages:0});
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
      const query=new URLSearchParams({page:String(page),size:"9",sortBy:applied.sortBy,direction:applied.direction});
      if(cat) query.set("categoryId",cat);
      if(applied.keyword) query.set("keyword",applied.keyword);
      if(applied.minPrice) query.set("minPrice",applied.minPrice);
      if(applied.maxPrice) query.set("maxPrice",applied.maxPrice);
      const data=await request(`/api/products?${query}`);
      setProducts(Array.isArray(data.content)?data.content:[]);
      setMeta({totalElements:data.totalElements||0,totalPages:data.totalPages||0});
    }catch(e){
      setProducts([]);
      setError(e.message);
    }finally{
      setLoading(false);
    }
  }

  useEffect(()=>{loadCategories()},[]);
  useEffect(()=>{loadProducts()},[cat,page,applied]);

  function search(e){e.preventDefault();setPage(0);setApplied({...filters})}
  function clearSearch(){
    const clean={keyword:"",minPrice:"",maxPrice:"",sortBy:"id",direction:"asc"};
    setFilters(clean);setApplied(clean);setCat("");setPage(0);
  }
  function chooseCategory(id){setCat(id);setPage(0)}

  return <main>
    <section className="hero">
      <h1>精選服飾</h1>
      <p>簡約・舒適・展現自我風格</p>
      <small>SIMPLE ・ COMFORTABLE ・ BE YOURSELF</small>
    </section>

    <form className="panel home-search" onSubmit={search}>
      <div className="home-search-title">
        <h2>查詢商品</h2><span>共找到 {meta.totalElements} 項</span>
      </div>
      <input aria-label="商品關鍵字" placeholder="搜尋商品名稱或描述..." value={filters.keyword}
        onChange={e=>setFilters({...filters,keyword:e.target.value})}/>
      <input aria-label="最低價格" type="number" min="0" placeholder="最低價格" value={filters.minPrice}
        onChange={e=>setFilters({...filters,minPrice:e.target.value})}/>
      <input aria-label="最高價格" type="number" min="0" placeholder="最高價格" value={filters.maxPrice}
        onChange={e=>setFilters({...filters,maxPrice:e.target.value})}/>
      <select aria-label="排序欄位" value={filters.sortBy} onChange={e=>setFilters({...filters,sortBy:e.target.value})}>
        <option value="id">商品編號</option><option value="name">商品名稱</option><option value="price">商品價格</option>
      </select>
      <select aria-label="商品種類" value={cat} onChange={e=>chooseCategory(e.target.value)}>
        <option value="">全部種類</option>
        {cats.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}
      </select>
      <button className="primary" type="submit">查詢</button>
      <button type="button" onClick={clearSearch}>清除條件</button>
    </form>

    {error&&
      <div className="api-error panel">
        <h3>後端資料目前無法載入</h3>
        <p>{error}</p>
        <p>請確認另一個 Terminal 正在執行 <code>mvn spring-boot:run</code>。</p>
        <button onClick={()=>{loadCategories();loadProducts()}}>重新連線</button>
      </div>
    }

    <div className="filters">
      <button className={!cat?"active":""} onClick={()=>chooseCategory("")}>全部商品</button>
      {cats.map(c=><button className={String(c.id)===String(cat)?"active":""}
        key={c.id} onClick={()=>chooseCategory(c.id)}>{c.name}</button>)}
    </div>

    <div className="heading">
      <h2>{cat?(cats.find(c=>String(c.id)===String(cat))?.name+" 商品"):"全部商品"}</h2>
      <span>每頁最多 9 項商品</span>
    </div>

    {loading&&<div className="panel"><p>商品載入中...</p></div>}

    {!loading&&!error&&products.length===0&&
      <div className="panel"><h3>找不到符合條件的商品</h3><p>請修改查詢條件，或按「清除條件」查看全部商品。</p></div>}

    {!loading&&products.length>0&&
      <section className="grid">
        {products.map(p=><article className="card" key={p.id}>
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

    {meta.totalPages>1&&<div className="pagination">
      <button disabled={page===0} onClick={()=>setPage(p=>p-1)}>上一頁</button>
      {Array.from({length:meta.totalPages},(_,i)=><button className={page===i?"active":""}
        onClick={()=>setPage(i)} key={i}>{i+1}</button>)}
      <button disabled={page+1>=meta.totalPages} onClick={()=>setPage(p=>p+1)}>下一頁</button>
    </div>}
  </main>;
}
